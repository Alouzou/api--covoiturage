package com.écovoiturage.écovoiturage.Exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
//Erreur 400
@ResponseStatus(HttpStatus.BAD_REQUEST)
class ErreurRequêteException(s: String) : RuntimeException(s){


}