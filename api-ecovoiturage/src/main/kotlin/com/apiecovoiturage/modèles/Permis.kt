package com.écovoiturage.écovoiturage.modèles

import com.écovoiturage.écovoiturage.Modèle.Utilisateur
import java.sql.Date

data class Permis(var numéro_permis: String,
                  var utilisateur: Utilisateur,
                  var date_émission: Long,
                  var date_expiration: Long,
                  var classe: String ) {
}

//Changé temporairement a string car date impossible a tester (prob permanent)