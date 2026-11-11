package edu.itc.shop.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import edu.itc.shop.api.dto.ProductDto;
import edu.itc.shop.api.dto.ProductV2;
import edu.itc.shop.api.dto.StockChange;
import edu.itc.shop.api.filter.Compress;
import edu.itc.shop.api.params.ProductFilter;
import edu.itc.shop.domain.Product;
import edu.itc.shop.domain.ProductService;
import edu.itc.shop.domain.ReviewRepository;

@Path("/products")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ProductResource {

    private final ProductService service;
    private final ReviewRepository reviewRepo;

    @Inject
    public ProductResource(ProductService service, ReviewRepository reviewRepo) {
        this.service = service;
        this.reviewRepo = reviewRepo;
    }

    @GET
    @Compress
    public Response list(@BeanParam @Valid ProductFilter f, @Context UriInfo uriInfo) {
        List<Product> all = service.search(
                f.q, f.minPrice, f.maxPrice, f.inStock, f.sortField(), f.descending());

        int total = all.size();
        int lastPage = Math.max(0, (total - 1) / f.size);
        List<ProductDto> page = all.stream()
                .skip((long) f.page * f.size)
                .limit(f.size)
                .map(ProductDto::from)
                .toList();

        var links = new ArrayList<Link>();
        links.add(pageLink(uriInfo, 0, "first"));
        if (f.page > 0) {
            links.add(pageLink(uriInfo, f.page - 1, "prev"));
        }
        if (f.page < lastPage) {
            links.add(pageLink(uriInfo, f.page + 1, "next"));
        }
        links.add(pageLink(uriInfo, lastPage, "last"));

        return Response.ok(new GenericEntity<List<ProductDto>>(page) {})
                .links(links.toArray(Link[]::new))
                .header("X-Total-Count", total)
                .build();
    }

    @GET
    @Path("{id: \\d+}")
    @Produces({"application/json; qs=1.0", "application/vnd.shop.v1+json; qs=0.9", MediaType.APPLICATION_XML + "; qs=0.9"})
    public Response one(@PathParam("id") long id, @Context Request request) {
        Product p = service.get(id);
        return conditionalItem(p, ProductDto.from(p), request);
    }

    @GET
    @Path("{id: \\d+}")
    @Produces("application/vnd.shop.v2+json; qs=0.5")
    public Response oneV2(@PathParam("id") long id, @Context Request request) {
        Product p = service.get(id);
        return conditionalItem(p, ProductV2.from(p), request);
    }

    @POST
    public Response create(@Valid @NotNull ProductDto dto, @Context UriInfo uriInfo) {
        Product saved = service.create(dto.toProduct(0, 0));
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(Long.toString(saved.id()))
                .build();
        return Response.created(location)
                .entity(ProductDto.from(saved))
                .tag(etag(saved))
                .build();
    }

    @PUT
    @Path("{id: \\d+}")
    public Response update(
            @PathParam("id") long id,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch,
            @Valid @NotNull ProductDto dto,
            @Context Request request
    ) {
        if (ifMatch == null || ifMatch.isBlank()) {
            throw new ClientErrorException("If-Match header is required", 428);
        }
        Product existing = service.get(id);
        Response.ResponseBuilder cond = request.evaluatePreconditions(etag(existing));
        if (cond != null) {
            throw new ClientErrorException("ETag does not match current version", Response.Status.PRECONDITION_FAILED);
        }
        Product updated = service.update(id, dto.toProduct(id, 0));
        return Response.ok(ProductDto.from(updated))
                .tag(etag(updated))
                .build();
    }

    @DELETE
    @Path("{id: \\d+}")
    public Response delete(@PathParam("id") long id) {
        service.delete(id);
        return Response.noContent().build();
    }

    @PATCH
    @Path("{id: \\d+}/stock")
    @Consumes(MediaType.APPLICATION_JSON)
    public ProductDto changeStock(@PathParam("id") long id, @Valid @NotNull StockChange change) {
        Product updated = service.adjustStock(id, change.delta());
        return ProductDto.from(updated);
    }

    @Path("{id: \\d+}/reviews")
    public ReviewResource reviews(@PathParam("id") long id) {
        return new ReviewResource(service.get(id), reviewRepo);
    }

    private static Link pageLink(UriInfo uriInfo, int page, String rel) {
        return Link.fromUriBuilder(uriInfo.getRequestUriBuilder().replaceQueryParam("page", page))
                .rel(rel)
                .build();
    }

    private static EntityTag etag(Product p) {
        return new EntityTag(Integer.toHexString(p.version()));
    }

    private static Response conditionalItem(Product p, Object entity, Request request) {
        EntityTag tag = etag(p);
        Response.ResponseBuilder cond = request.evaluatePreconditions(tag);
        if (cond != null) {
            return cond.tag(tag).build();
        }
        var cc = new CacheControl();
        cc.setPrivate(true);
        cc.setMaxAge(60);
        cc.setNoTransform(true);
        return Response.ok(entity)
                .tag(tag)
                .cacheControl(cc)
                .build();
    }
}
