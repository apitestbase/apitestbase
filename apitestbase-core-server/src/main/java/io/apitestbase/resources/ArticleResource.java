package io.apitestbase.resources;

import io.apitestbase.db.ArticleDAO;
import io.apitestbase.models.Article;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/articles") @Produces({ MediaType.APPLICATION_JSON })
public class ArticleResource {
    private final ArticleDAO dao;

    public ArticleResource(ArticleDAO dao) {
        this.dao = dao;
    }

    @POST
    public Article create(Article article) {
        long articleId = dao.insert(article);
        return findById(articleId);
    }

    @PUT @Path("{articleId}")
    public Article update(Article article, @PathParam("articleId") long articleId) {
        dao.update(article);
        return findById(articleId);
    }

    @DELETE @Path("{articleId}")
    public void delete(@PathParam("articleId") long articleId) {
        dao.deleteById(articleId);
    }

    @GET
    public List<Article> findAll() {
        return dao.findAll();
    }

    @GET @Path("{articleId}")
    public Article findById(@PathParam("articleId") long articleId) {
        return dao.findById(articleId);
    }
}
