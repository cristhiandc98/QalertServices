package qalert.com.models.suggestions;

import qalert.com.utils.utils.RegexUtil;

public class SuggestionsModel {

    private Long userId;

    private Integer suggestionsTypeId;

    private String suggestion;



    public String validateInsert() {
    
        if (!RegexUtil.validateNumericId(userId)) { return "Id invalido."; }
        if (!RegexUtil.validateNumeric(suggestionsTypeId)) { return "Id invalido."; }

        if (suggestion == null || suggestion.trim().isEmpty()) { return "El comentario no puede estar vacío.";}
        if (!RegexUtil.SUGGESTION_FIELD.matcher(suggestion).matches()){return "Escribe un comentario de al menos 10 letras.";}

        return null;
    }



    public Integer getSuggestionsTypeId() {
        return suggestionsTypeId;
    }

    public void setSuggestionsTypeId(Integer suggestionsTypeId) {
        this.suggestionsTypeId = suggestionsTypeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestionField) {
        this.suggestion = suggestionField;
    }
}
