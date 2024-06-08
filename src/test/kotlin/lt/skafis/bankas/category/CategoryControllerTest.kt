package lt.skafis.bankas.category
//
//import com.google.api.core.ApiFuture
//import com.google.cloud.Timestamp
//import com.google.cloud.firestore.*
//import lt.skafis.bankas.model.Category
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Assertions.assertNull
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.mockito.ArgumentMatchers.any
//import org.mockito.ArgumentMatchers.anyString
//import org.mockito.Mockito
//import org.mockito.Mockito.`when`
//import org.springframework.boot.test.context.SpringBootTest
//
//@SpringBootTest
//class CategoryControllerTest {
//
//    private lateinit var firestore: Firestore
//    private lateinit var collectionReference: CollectionReference
//    private lateinit var categoryController: CategoryController
//
//    @BeforeEach
//    fun setUp() {
//        firestore = Mockito.mock(Firestore::class.java)
//        collectionReference = Mockito.mock(CollectionReference::class.java)
//        `when`(firestore.collection("categories")).thenReturn(collectionReference)
//        categoryController = CategoryController(firestore)
//    }
//
//    @Test
//    fun `should get category by id`() {
//        val category = Category("Math. Algebra", "Math problems related to algebra", "crow123", "2014-01-01T00:00:00Z")
//        val documentSnapshot = Mockito.mock(DocumentSnapshot::class.java).apply {
//            `when`(exists()).thenReturn(true)
//            `when`(toObject(Category::class.java)).thenReturn(category)
//        }
//
//        val documentReference = Mockito.mock(DocumentReference::class.java)
//        @Suppress("UNCHECKED_CAST")
//        val apiFuture = Mockito.mock(ApiFuture::class.java) as ApiFuture<DocumentSnapshot>
//        `when`(apiFuture.get()).thenReturn(documentSnapshot)
//        `when`(documentReference.get()).thenReturn(apiFuture)
//        `when`(collectionReference.document(anyString())).thenReturn(documentReference)
//
//        val result = categoryController.getCategory("3a2560e0-15ab-4027-9a58-118a6d878cd7")
//        assertEquals(category, result)
//    }
//
//    @Test
//    fun `should return null when category not found`() {
//        val documentSnapshot = Mockito.mock(DocumentSnapshot::class.java).apply {
//            `when`(exists()).thenReturn(false)
//        }
//
//        val documentReference = Mockito.mock(DocumentReference::class.java)
//        @Suppress("UNCHECKED_CAST")
//        val apiFuture = Mockito.mock(ApiFuture::class.java) as ApiFuture<DocumentSnapshot>
//        `when`(apiFuture.get()).thenReturn(documentSnapshot)
//        `when`(documentReference.get()).thenReturn(apiFuture)
//        `when`(collectionReference.document(anyString())).thenReturn(documentReference)
//
//        val result = categoryController.getCategory("non-existent-id")
//        assertNull(result)
//    }
//
//    @Test
//    fun `should create category`() {
//        val category = Category("Math. Algebra", "Math problems related to algebra", "crow123", "2014-01-01T00:00:00Z")
//        val writeResult = Mockito.mock(WriteResult::class.java).apply {
//            `when`(updateTime).thenReturn(Timestamp.now())
//        }
//        @Suppress("UNCHECKED_CAST")
//        val apiFuture = Mockito.mock(ApiFuture::class.java) as ApiFuture<WriteResult>
//        `when`(apiFuture.get()).thenReturn(writeResult)
//
//        val documentReference = Mockito.mock(DocumentReference::class.java)
//        `when`(documentReference.set(any(Category::class.java))).thenReturn(apiFuture)
//        `when`(collectionReference.document()).thenReturn(documentReference)
//
//        val result = categoryController.createCategory(category)
//        assertEquals(writeResult.updateTime.toString(), result)
//    }
//
//    @Test
//    fun `should update category`() {
//        val category = Category("Math. Algebra", "Math problems related to algebra", "crow123", "2014-01-01T00:00:00Z")
//        val writeResult = Mockito.mock(WriteResult::class.java).apply {
//            `when`(updateTime).thenReturn(Timestamp.now())
//        }
//        @Suppress("UNCHECKED_CAST")
//        val apiFuture = Mockito.mock(ApiFuture::class.java) as ApiFuture<WriteResult>
//        `when`(apiFuture.get()).thenReturn(writeResult)
//
//        val documentReference = Mockito.mock(DocumentReference::class.java)
//        `when`(documentReference.set(any(Category::class.java))).thenReturn(apiFuture)
//        `when`(collectionReference.document(anyString())).thenReturn(documentReference)
//
//        val result = categoryController.updateCategory("3a2560e0-15ab-4027-9a58-118a6d878cd7", category)
//        assertEquals(writeResult.updateTime.toString(), result)
//    }
//
//    @Test
//    fun `should delete category`() {
//        val writeResult = Mockito.mock(WriteResult::class.java).apply {
//            `when`(updateTime).thenReturn(Timestamp.now())
//        }
//        @Suppress("UNCHECKED_CAST")
//        val apiFuture = Mockito.mock(ApiFuture::class.java) as ApiFuture<WriteResult>
//        `when`(apiFuture.get()).thenReturn(writeResult)
//
//        val documentReference = Mockito.mock(DocumentReference::class.java)
//        `when`(documentReference.delete()).thenReturn(apiFuture)
//        `when`(collectionReference.document(anyString())).thenReturn(documentReference)
//
//        val result = categoryController.deleteCategory("3a2560e0-15ab-4027-9a58-118a6d878cd7")
//        assertEquals(writeResult.updateTime.toString(), result)
//    }
//}
