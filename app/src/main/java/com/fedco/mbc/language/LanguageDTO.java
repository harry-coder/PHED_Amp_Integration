package com.fedco.mbc.language;

/**
 * Created by Hasnain on 09-Jan-18.
 */

public class LanguageDTO {
    private String languageName;
    private String languageCode;

    public LanguageDTO(String languageName, String languageCode) {
        this.languageName = languageName;
        this.languageCode = languageCode;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getLanguageCode() {
        return languageCode;
    }
}
