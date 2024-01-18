package com.écovoiturage.écovoiturage.modèles

data class Adresse (
               var id: Int,
               var appartement: String?,
               var numéro_principal: String,
               var rue: String,
               var ville :String,
               var province : String,
               var code_postal : String,
               var pays : String )  {
}