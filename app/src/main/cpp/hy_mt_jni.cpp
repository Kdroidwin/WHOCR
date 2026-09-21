#include <jni.h>
#include <android/log.h>
#include <mutex>
#include <stdexcept>
#include <string>
#include <vector>

#include "llama.h"

namespace {
constexpr const char * kTag = "WHOCR-HyMT";
std::mutex g_mutex;
llama_model * g_model = nullptr;
std::string g_model_path;

void llama_android_logger(enum ggml_log_level level, const char * text, void *) {
    const int priority = level >= GGML_LOG_LEVEL_ERROR ? ANDROID_LOG_ERROR
        : level == GGML_LOG_LEVEL_WARN ? ANDROID_LOG_WARN
        : ANDROID_LOG_INFO;
    __android_log_print(priority, kTag, "%s", text);
}

std::string jstring_to_utf8(JNIEnv * env, jstring value) {
    const char * chars = env->GetStringUTFChars(value, nullptr);
    if (chars == nullptr) throw std::runtime_error("Could not read text");
    std::string result(chars);
    env->ReleaseStringUTFChars(value, chars);
    return result;
}

void throw_illegal_state(JNIEnv * env, const std::string & message) {
    __android_log_print(ANDROID_LOG_ERROR, kTag, "%s", message.c_str());
    jclass exception = env->FindClass("java/lang/IllegalStateException");
    if (exception != nullptr) env->ThrowNew(exception, message.c_str());
}

llama_model * load_model(const std::string & path) {
    if (g_model != nullptr && g_model_path == path) return g_model;
    if (g_model != nullptr) {
        llama_model_free(g_model);
        g_model = nullptr;
    }
    llama_model_params params = llama_model_default_params();
    params.n_gpu_layers = 0;
    params.load_mode = LLAMA_LOAD_MODE_MMAP;
    __android_log_print(ANDROID_LOG_INFO, kTag, "Loading Hy-MT from %s", path.c_str());
    g_model = llama_model_load_from_file(path.c_str(), params);
    if (g_model == nullptr) throw std::runtime_error("Hy-MT model could not be loaded");
    g_model_path = path;
    return g_model;
}

std::string translate(const std::string & path, const std::string & source, const std::string & target) {
    llama_model * model = load_model(path);
    const llama_vocab * vocab = llama_model_get_vocab(model);
    // Hy-MT stores a Jinja chat template in its GGUF.  The native library is
    // intentionally linked without llama.cpp's large common/Jinja component,
    // so emit this model's documented single-user form directly.  Omitting
    // these boundary tokens makes the model continue from a raw prompt and
    // commonly degenerates into repeated punctuation.
    const std::string instruction = "Translate the following segment into " + target +
        ", without additional explanation：" + source;
    const std::string prompt =
        "<｜hy_begin▁of▁sentence｜><｜hy_User｜>" + instruction + "<｜hy_Assistant｜>";
    const int token_count = -llama_tokenize(vocab, prompt.c_str(), prompt.size(), nullptr, 0, true, true);
    if (token_count <= 0 || token_count > 3072) throw std::runtime_error("Text is too long to translate");
    std::vector<llama_token> tokens(token_count);
    if (llama_tokenize(vocab, prompt.c_str(), prompt.size(), tokens.data(), tokens.size(), true, true) < 0) {
        throw std::runtime_error("Could not tokenize translation text");
    }

    llama_context_params context_params = llama_context_default_params();
    context_params.n_ctx = static_cast<uint32_t>(token_count + 768);
    context_params.n_batch = static_cast<uint32_t>(token_count);
    context_params.n_threads = 4;
    context_params.n_threads_batch = 4;
    llama_context * context = llama_init_from_model(model, context_params);
    if (context == nullptr) throw std::runtime_error("Could not create Hy-MT context");

    llama_sampler * sampler = llama_sampler_chain_init(llama_sampler_chain_default_params());
    llama_sampler_chain_add(sampler, llama_sampler_init_greedy());
    llama_batch batch = llama_batch_get_one(tokens.data(), tokens.size());
    std::string output;
    try {
        for (int generated = 0; generated < 512; ++generated) {
            if (llama_decode(context, batch) != 0) throw std::runtime_error("Hy-MT inference failed");
            llama_token token = llama_sampler_sample(sampler, context, -1);
            if (llama_vocab_is_eog(vocab, token)) break;
            char piece[512];
            const int count = llama_token_to_piece(vocab, token, piece, sizeof(piece), 0, true);
            if (count < 0) throw std::runtime_error("Could not decode Hy-MT output");
            output.append(piece, count);
            llama_sampler_accept(sampler, token);
            batch = llama_batch_get_one(&token, 1);
        }
    } catch (...) {
        llama_sampler_free(sampler);
        llama_free(context);
        throw;
    }
    llama_sampler_free(sampler);
    llama_free(context);
    return output;
}
} // namespace

extern "C" JNIEXPORT jstring JNICALL
Java_com_easyocr_editor_translation_HyMtNative_translate(
    JNIEnv * env,
    jclass,
    jstring model_path,
    jstring source_text,
    jstring target_language
) {
    std::lock_guard<std::mutex> lock(g_mutex);
    try {
        llama_log_set(llama_android_logger, nullptr);
        llama_backend_init();
        const std::string result = translate(
            jstring_to_utf8(env, model_path),
            jstring_to_utf8(env, source_text),
            jstring_to_utf8(env, target_language)
        );
        return env->NewStringUTF(result.c_str());
    } catch (const std::exception & error) {
        throw_illegal_state(env, error.what());
        return nullptr;
    }
}
