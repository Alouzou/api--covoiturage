package com.écovoiturage.écovoiturage.Exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

// Erreur 409

@ResponseStatus(HttpStatus.CONFLICT)

class ProduitExisteDéjàException(s: String?) : RuntimeException(s){

}