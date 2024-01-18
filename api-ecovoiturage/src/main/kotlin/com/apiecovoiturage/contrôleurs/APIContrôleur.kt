package com.écovoiturage.écovoiturage.contrôleurs

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class APIContrôleur {
    @GetMapping("/")
    fun index() = "Écovoiturage"
}