
import com.example.snippets.entity.Snippet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SnippetRepository : JpaRepository<Snippet, Long> {
  override fun findAll(): List<Snippet>
  fun findByTitle(title: String): List<Snippet>
}
