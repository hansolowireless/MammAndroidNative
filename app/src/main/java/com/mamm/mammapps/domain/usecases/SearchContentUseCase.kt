package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.domain.interfaces.CustomContentRepository
import com.mamm.mammapps.ui.model.search.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchContentUseCase @Inject constructor(
    private val repository: CustomContentRepository
) {
    /**
     * Invoca el caso de uso para buscar contenido.
     * Devuelve un Flow que emite los diferentes estados de la operación: Loading, Success o Error.
     * @param query El término de búsqueda.
     * @return Un Flow que emite un Resource con la lista de contenido.
     */
    operator fun invoke(query: String): Flow<Resource<List<Bookmark>>> = flow {
        // Emitir el estado de carga inmediatamente
        emit(Resource.Loading())

        // Lógica de negocio: evitar búsquedas vacías o muy cortas para no saturar la API
        if (query.length < 3) {
            emit(Resource.Success(emptyList()))
            return@flow
        }

        // Encapsular la llamada al repositorio, que puede fallar
        runCatching {
            // 2. Llamar al repositorio y obtener el resultado.
            // AÑADIMOS .getOrThrow() para desenvolver el Result.
            repository.searchContent(query).getOrThrow()
        }.fold(
            onSuccess = { bookmarks ->
                // 3. Si tiene éxito, emitir el resultado
                // Ahora 'bookmarks' es de tipo List<Bookmark>, como se espera.
                emit(Resource.Success(bookmarks))
            },
            onFailure = { exception ->
                // 4. Si falla (ya sea en la llamada de red o por el getOrThrow),
                // determinar el mensaje de error adecuado y emitirlo
                val errorMessage = when (exception) {
                    is retrofit2.HttpException -> "Error de red: ${exception.code()}"
                    is java.io.IOException -> "Error de conexión. Revisa tu acceso a internet."
                    else -> exception.message ?: "Ha ocurrido un error inesperado."
                }
                emit(Resource.Error(errorMessage))
            }
        )
    }
}