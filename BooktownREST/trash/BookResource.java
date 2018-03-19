package edu.asupoly.ser422.restexample.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asupoly.ser422.restexample.model.Book;
import edu.asupoly.ser422.restexample.model.Author;
import edu.asupoly.ser422.restexample.services.BooktownService;
import edu.asupoly.ser422.restexample.services.BooktownServiceFactory;

/*
public List<Book> getBooks();
public Book getBook(int id);
//public boolean deleteAuthor(int authorId); //
public int createBook(String title, int aid, int sid);
public Author findAuthorOfBook(int bookId);
*/

@Path("/books")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class BookResource {
	private static BooktownService __bService = BooktownServiceFactory.getInstance();

	// Technique for location header taken from
	// http://usna86-techbits.blogspot.com/2013/02/how-to-return-location-header-from.html
	@Context
	private UriInfo _uriInfo;

	 /**
     * @apiDefine BadRequestError
     * @apiError (Error 4xx) {400} BadRequest Bad Request Encountered
     * */
    /** @apiDefine ActivityNotFoundError
     * @apiError (Error 4xx) {404} NotFound Activity cannot be found
     * */
    /**
     * @apiDefine InternalServerError
     * @apiError (Error 5xx) {500} InternalServerError Something went wrong at server, Please contact the administrator!
     * */
    /**
     * @apiDefine NotImplementedError
     * @apiError (Error 5xx) {501} NotImplemented The resource has not been implemented. Please keep patience, our developers are working hard on it!!
     * */

    /**
     * @api {get} /authors Get list of Authors
     * @apiName getAuthors
     * @apiGroup Authors
     *
     * @apiUse BadRequestError
     * @apiUse InternalServerError
     *
     * @apiSuccessExample Success-Response:
     * 	HTTP/1.1 200 OK
     * 	[
     *   {"authorId":1111,"firstName":"Ariel","lastName":"Denham"},
     *   {"authorId":1212,"firstName":"John","lastName":"Worsley"}
     *  ]
     *
     * */
	@GET
	public List<Book> getBooks() {
		return __bService.getBooks();
	}

	/* This is the first version of GET we did, using defaults and letting Jersey internally serialize
	 @GET
	@Path("/{authorId}")
	public Author getAuthor(@PathParam("authorId") int aid) {
		return __bService.getAuthor(aid); //1st version --jersey serialize Author object.
	}
	 */
	/*
	 * This is a second version - it uses Jackson's default mapping via ObjectMapper, which spits out
	 * the same JSON as Jersey's internal version, so the output will look the same as version 1 when you run
	 */
	@GET
	@Path("/{bookId}")
	public Response getBook(@PathParam("bookId") int bid) {
		// This isn't correct - what if the authorId is not for an active author?
		Book book = __bService.getBook(bid);
		// let's use Jackson instead. ObjectMapper will build a JSON string and we use
		// the ResponseBuilder to use that. Note the result looks the same
		try {
			String aString = new ObjectMapper().writeValueAsString(book); //2nd version
			return Response.status(Response.Status.OK).entity(aString).build();
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}

	// This is a 3rd version using a custom serializer I've encapsulated over in the new helper class
	/*
	 * @GET

	@Path("/{authorId}")
	public Response getAuthor(@PathParam("authorId") int aid) {
		Author author = __bService.getAuthor(aid);

		// AuthorSerializationHelper will build a slightly different JSON string and we still use
		// the ResponseBuilder to use that. The key property names are changed in the result.
		try {
			String aString = AuthorSerializationHelper.getHelper().generateJSON(author); //3rd version
			return Response.status(Response.Status.OK).entity(aString).build();
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}
	*/
	/* This was the first version of POST we did
	@POST
	@Consumes("text/plain")
    public int createAuthor(String name) {
		String[] names = name.split(" ");
		// not handled - what if this returns -1?
		int aid = __bService.createAuthor(names[0], names[1]);
		return aid;
    }
    */
	/*
	 * This was the second version that added simple custom response headers and payload
	 */
	@POST
	@Consumes("text/plain")
    public Response createBook(String title, int aid, int sid) { //author had String names
//		String[] names = name.split(" ");
		int bid = -1;
//		int bid = __bService.createBook(names[0], names[1]);
		if (bid == -1) {
			return Response.status(500).entity("{ \" EXCEPTION INSERTING INTO DATABASE! \"}").build();
		} else if (bid == 0) {
			return Response.status(500).entity("{ \" ERROR INSERTING INTO DATABASE! \"}").build();
		}
		return Response.status(201)
				.header("Location", String.format("%s/%s",_uriInfo.getAbsolutePath().toString(), bid))
				.entity("{ \"Book\" : \"" + bid + "\"}").build();
    }

		/*more methods*/
		//This is the first version of GET we did, using defaults and letting Jersey internally serialize
		@GET
		@Path("/{bookId}/author")
		public Author findAuthorOfBook (@PathParam("bookId") int bid) {
			return __bService.findAuthorOfBook(bid); //1st version --jersey serialize Author object.
		}
		/*
		 * This is a second version - it uses Jackson's default mapping via ObjectMapper, which spits out
		 * the same JSON as Jersey's internal version, so the output will look the same as version 1 when you run
		 */
		 /*
		@GET
		@Path("/{bookId}")
		public Response getBook(@PathParam("bookId") int bid) {
			// This isn't correct - what if the authorId is not for an active author?
			Book book = __bService.getBook(bid);
			// let's use Jackson instead. ObjectMapper will build a JSON string and we use
			// the ResponseBuilder to use that. Note the result looks the same
			try {
				String aString = new ObjectMapper().writeValueAsString(book); //2nd version
				return Response.status(Response.Status.OK).entity(aString).build();
			} catch (Exception exc) {
				exc.printStackTrace();
				return null;
			}
		}
		*/
}
