package com.sporty.f1bet.controller.exception;

public record ApiError(String title, int status, String detail, String instance) {}
