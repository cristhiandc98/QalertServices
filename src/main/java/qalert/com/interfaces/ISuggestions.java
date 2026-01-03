package qalert.com.interfaces;

import qalert.com.models.generic.Response2;
import qalert.com.models.suggestions.SuggestionsModel;

public interface ISuggestions {

    Response2<Boolean> insert(SuggestionsModel request);

}
