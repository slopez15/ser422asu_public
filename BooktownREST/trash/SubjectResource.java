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

import edu.asupoly.ser422.restexample.model.Subject;
import edu.asupoly.ser422.restexample.model.Book;
import edu.asupoly.ser422.restexample.services.BooktownService;
import edu.asupoly.ser422.restexample.services.BooktownServiceFactory;

/*
// Subject methods
public List<Subject> getSubjects();
public Subject getSubject(int id);
//public boolean updateAuthor(Author author); //location
public List<Book> findBooksBySubject(int subjectId);

*/

@Path("/subjects")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class SubjectResource {
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
	public List<Subject> getSubjects() {
		return __bService.getSubjects();
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
	@Path("/{subjectId}")
	public Response getSubject(@PathParam("subjectId") int sid) {
		// This isn't correct - what if the authorId is not for an active author?
		Subject subject = __bService.getSubject(sid);
		// let's use Jackson instead. ObjectMapper will build a JSON string and we use
		// the ResponseBuilder to use that. Note the result looks the same
		try {
			String aString = new ObjectMapper().writeValueAsString(subject); //2nd version
			return Response.status(Response.Status.OK).entity(aString).build();
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}

	/*more methods*/
	// This is the first version of GET we did, using defaults and letting Jersey internally serialize
	 @GET
	@Path("/{subjectId}/books") //"/{bookId}/author"
	public List<Book> findBooksBySubject (@PathParam("subjectId") int sid) {
		return __bService.findBooksBySubject(sid); //1st version --jersey serialize List<Books> object.
	}
	//*/
	/*
	 * This is a second version - it uses Jackson's default mapping via ObjectMapper, which spits out
	 * the same JSON as Jersey's internal version, so the output will look the same as version 1 when you run
	 */
	 /*
	@GET
	@Path("/{subjectId}")
	public Response getSubject(@PathParam("subjectId") int sid) {
		// This isn't correct - what if the authorId is not for an active author?
		Subject subject = __bService.getSubject(sid);
		// let's use Jackson instead. ObjectMapper will build a JSON string and we use
		// the ResponseBuilder to use that. Note the result looks the same
		try {
			String aString = new ObjectMapper().writeValueAsString(subject); //2nd version
			return Response.status(Response.Status.OK).entity(aString).build();
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}
	*/
}
