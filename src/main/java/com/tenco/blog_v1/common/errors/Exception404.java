package com.tenco.blog_v1.common.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class Exception404 extends RuntimeException {

    public Exception404(String msg) {
        super(msg);
    }

}
