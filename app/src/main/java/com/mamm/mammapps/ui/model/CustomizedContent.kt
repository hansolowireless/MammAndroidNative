package com.mamm.mammapps.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*Se usa para distinguir los eventos añadidos a la home pero que no vienen en la respuesta de la home
* con el fin de saber dónde buscarlos al pasar de ContentEntityUI a ContentToPlayUI
*/
@Parcelize
sealed class CustomizedContent: Parcelable {
    object None: CustomizedContent()
    object MostWatchedType : CustomizedContent()
    object RecommendedType : CustomizedContent()
    object BookmarkType : CustomizedContent()
}
