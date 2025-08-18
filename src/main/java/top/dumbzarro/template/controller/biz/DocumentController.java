package top.dumbzarro.template.controller.biz;//package top.dumbzarro.template.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Tag(name = "文档控制器", description = "文档相关接口")
//@RestController
//@RequestMapping("/document")
//@RequiredArgsConstructor
//public class DocumentController {
//
//    private final VectorStore vectorStore;
//
//    @Operation(description = "embedding")
//    @PostMapping("/embedding")
//    public Boolean embedding(@RequestParam MultipartFile file) {
//        // 从IO流中读取文件,并将文本内容划分成更小的块
//        // List<Document> splitDocuments = new TokenTextSplitter()
//        // .apply(tikaDocumentReader.read());
//        List<Document> splitDocuments = getDocument(file);
//        // 存入向量数据库，这个过程会自动调用embeddingModel,将文本变成向量再存入。
//        vectorStore.add(splitDocuments);
//
//        return true;
//    }
//
//    private List<Document> getDocument(MultipartFile file) {
//        return new ArrayList<>();
//    }
//
//}
