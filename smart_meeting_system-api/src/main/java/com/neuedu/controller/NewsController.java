package com.neuedu.controller;

import com.neuedu.pojo.News;
import com.neuedu.pojo.Result;
import com.neuedu.service.NewsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("news")
@CrossOrigin
public class NewsController {
    @Autowired
    NewsService newsService;

    @GetMapping()
    public Result getNews(@RequestParam Integer pageNum,
                          @RequestParam Integer pageSize,
                          @RequestParam(required = false) String title,
                          @RequestParam(required = false) String imageUrl,
                          @RequestParam(required = false) String author,
                          @RequestParam(required = false) String summary,
                          @RequestParam(required = false) String order
    ) {
        Result result = newsService.getNews(pageNum,pageSize,title,imageUrl,author,summary,order);
        return result;
    }

    @PostMapping()
    public Result addNews(@RequestBody News news) {
        Result result = newsService.addNews(news);
        return result;
    }

    @DeleteMapping()
    public Result deleteNews(@RequestParam Long id) {
        Result result = newsService.deleteNews(id);
        return result;
    }

    @PutMapping()
    public Result updateNews(@RequestBody News news) {
        Result result = newsService.updateNews(news);
        return result;
    }
    @GetMapping("tenant")
    public Result getTenants() {
        Result result = newsService.getTenants();
        return result;
    }
    // 批量删除
    @PostMapping("/batchDelete")
    public Result batchDelete(@RequestBody List<Long> ids) {
        return newsService.batchDelete(ids);
    }

    // 导出选中
    @GetMapping("/exportSelected")
    public void exportSelected(@RequestParam String ids, HttpServletResponse resp) throws IOException {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .toList();
        newsService.exportByIds(idList, resp);
    }
    // 导出
    @GetMapping("/export")
    public void exportBySearch(@RequestParam(required = false) String title,
                               @RequestParam(required = false) String imageUrl,
                               @RequestParam(required = false) String author,
                               @RequestParam(required = false) String summary,
                               @RequestParam(required = false) String order,
                               HttpServletResponse resp) throws IOException {
        newsService.exportBySearch(title,imageUrl,author,summary,order,resp);
    }


}
