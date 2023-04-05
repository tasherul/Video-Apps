package com.code.files.utils.parser;

import java.util.List;

public interface LinkParserCallback {
    void onSuccess(List<Stream> result);
    void onError(String error);
}
