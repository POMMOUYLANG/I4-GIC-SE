package edu.itc.shop.api;

import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import edu.itc.shop.api.dto.CreateReview;
import edu.itc.shop.domain.Product;
import edu.itc.shop.domain.Review;
import edu.itc.shop.domain.ReviewRepository;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewResource {

    private final Product product;
    private final ReviewRepository repo;

    public ReviewResource(Product product, ReviewRepository repo) {
        this.product = product;
        this.repo = repo;
    }

    @GET
    public List<Review> list() {
        return repo.findByProductId(product.id());
    }

    @POST
    public Response create(@Valid @NotNull CreateReview dto, @Context UriInfo uriInfo) {
        Review saved = repo.save(new Review(0, product.id(), dto.stars(), dto.author(), dto.text()));
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(Long.toString(saved.id()))
                .build();
        return Response.created(location).entity(saved).build();
    }
}
