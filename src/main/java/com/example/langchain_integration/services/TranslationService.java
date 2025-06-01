package com.example.langchain_integration.services;

import com.example.langchain_integration.augmentors.TranslateResponseAugmentor;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

@RegisterAiService(retrievalAugmentor = TranslateResponseAugmentor.class)
@ApplicationScoped
public interface TranslationService {

    @SystemMessage("""
            You are a language translator service. You clearly and directly translate
            the provided messages in the requested languages. Sometimes, when the messages are written in German,
            they may not include proper umlauts or the letter ß; instead, these are replaced with 'ae', 'ue', etc or 'ss'.
            """)
    @UserMessage("""
                Respond clearly with the direct translation of this message: '{message}' in '{language}' language, disregarding the meaning of the message.
                    If you don't know the language of the original message just say: I don't know the language of this message!.
                    If you don't know the language you are supposed to translate to, just say: I don't know this message!.
                    If the message is not provided then say: This message is empty!'
            """)
    Multi<String> translate(String message, String language);
}