package lt.skafis.bankas.service
//
//import lt.skafis.bankas.model.ProblemMeta
//import lt.skafis.bankas.repository.FirestoreMetaRepository
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.mockito.Mockito
//import org.mockito.Mockito.`when`
//import org.springframework.boot.test.context.SpringBootTest
//import org.webjars.NotFoundException
//
//@SpringBootTest
//class ProblemMetaServiceTest {
//
//    private lateinit var firestoreMetaRepository: FirestoreMetaRepository
//    private lateinit var problemMetaService: ProblemMetaService
//
//    @BeforeEach
//    fun setUp() {
//        firestoreMetaRepository = Mockito.mock(FirestoreMetaRepository::class.java)
//        problemMetaService = ProblemMetaServiceImpl(firestoreMetaRepository)
//    }
//
//    @Test
//    fun `should return last used SKF code`() {
//        val problemMeta = ProblemMeta("lastUsedCode123")
//        `when`(firestoreMetaRepository.getProblemMeta()).thenReturn(problemMeta)
//
//        val result = problemMetaService.getLastUsedSkfCode()
//        assertEquals("lastUsedCode123", result)
//    }
//
//    @Test
//    fun `should throw NotFoundException when problem meta not found`() {
//        `when`(firestoreMetaRepository.getProblemMeta()).thenReturn(null)
//
//        val exception = assertThrows(NotFoundException::class.java) {
//            problemMetaService.getLastUsedSkfCode()
//        }
//        assertEquals("Problem meta with last used SKF code not found", exception.message)
//    }
//
//    @Test
//    fun `should throw NotFoundException when last used SKF code is blank`() {
//        val problemMeta = ProblemMeta("")
//        `when`(firestoreMetaRepository.getProblemMeta()).thenReturn(problemMeta)
//
//        val exception = assertThrows(NotFoundException::class.java) {
//            problemMetaService.getLastUsedSkfCode()
//        }
//        assertEquals("Last used SKF code in problemMeta empty or not found", exception.message)
//    }
//}
