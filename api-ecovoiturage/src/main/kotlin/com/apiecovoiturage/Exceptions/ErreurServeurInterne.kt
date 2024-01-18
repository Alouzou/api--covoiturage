package com.écovoiturage.écovoiturage.Exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
//erreur dans la requete
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class ErreurServeurInterne(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause){
}