package com.mercaditocloud.bff.exception;

import java.util.List;

public record ApiError(int status, String mensaje, List<String> detalles) {
}
