package edu.asupoly.ser422.restexample.services.impl;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import edu.asupoly.ser422.restexample.model.Author;
import edu.asupoly.ser422.restexample.model.Book;
import edu.asupoly.ser422.restexample.model.Subject;
import edu.asupoly.ser422.restexample.services.BooktownService;

import java.util.Properties;
import java.util.Random;

/*
SimpleBooktownServiceImpl implementations of BooktownService.
BooktownService <i> 7 defined:
//Book methods
1. public List<Book> getBooks();
2. public Book getBook(int id);
3. public int createBook(String title, int aid, int sid);
4. public Author findAuthorOfBook(int bookId);
//Subject methods
5. public List<Subject> getSubjects();
6. public Subject getSubject(int id);
7. public List<Book> findBooksBySubject(int subjectId);

Goals:
A) (35) Implement Book and Subject resources, just like we did with AuthorResource, and support the 7 methods above.; Book and Subject, AuthorResource
B. (10) Add a REST interface to your API that DELETES a Book.;
	/rest/books/{bid}
C. (10) REST interface to your API that UPDATES the Subject's Location.;
	/rest/subjects/{sid}
D. (15) Add a convenience ENDPOINT that, given a Subject location substring, finds all Authors who author a book in that Subject.;
	endpoint: /RestExampleAPI/rest/
	{subject's locations}<--what's the purpose? (ex:"Dallas, TX"). Probably all authors in location && did certain subject.; wo/location, authors in subject.
	/rest/subjects/{sid}/locations/{location} -->authors who did subject in location
E. (10) Complete the implementation of the RDBMBooktownServiceImpl (all of the TODO methods).
	The API should run correctly with only a property change in dao.properties.;
	**dao.properties?
F. (10) Add robust error handling. You will see a number of swallowed exceptions (exceptions whose catch blocks are empty),
	use of generic exceptions, missing custom error messages for various client request errors (namely 400, 405, 415), and a lack
	of consideration for error scenarios. Identify and document (using an inline code comment prefixed with "LAB 3 TASK 6:") 3 places
	that you have implemented an error-handling scenario that affects the response to a REST API request.
	**generic and custom exceptions w/messages (consider code [400,405,415,] & scenarios) "LAB 3 TASK 6:"
G. (10)Add API documentation using the apidocjs tool available at apidocjs.com. The API documentation should be
	comprehensive, meaning include all methods, all verbs you process, all errors, and example response formats. Here is an
	example of good Api documentation using this tool: http://topia-env.ec2n87mrb8.us-west-2.elasticbeanstalk.com/apidocs/
	**apidocjs.com w/ methods, HTTP verbs, erros, response formats. (ex: http://topia-env.ec2n87mrb8.us-west-2.elasticbeanstalk.com/apidocs/ )

What's different between SimpleBooktownServiceImpl and RDBMBooktownServiceImpl?
SimpleBooktownServiceImpl uses hard coded values
RDBMBooktownServiceImpl uses database to pull values
__dbProperties for sql queries.
	we don't want queries in our code.

*/
/*
Author of indicated: Saul Lopez
Produced: (Book & Subject related methods--anything below updateAuthor)
*/


//A simple impl (RDBM style) of interface BooktownService
public class RDBMBooktownServiceImpl extends ABooktownServiceImpl {
	private static Properties __dbProperties;//to get properties in BooktownREST/properties/rdbm.properties
	private static String __jdbcUrl;
	private static String __jdbcUser;
	private static String __jdbcPasswd;
	private static String __jdbcDriver;

	private Connection getConnection() throws Exception {
		try {
			Class.forName(__jdbcDriver);
			return DriverManager.getConnection(__jdbcUrl, __jdbcUser, __jdbcPasswd);
		} catch (Exception exc) {
			throw exc;
		}
	}

	// Only instantiated by factory within package scope
	public RDBMBooktownServiceImpl() {
	}

	public List<Author> getAuthors() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<Author> rval = new ArrayList<Author>();
		try {
			conn = getConnection();

			stmt = conn.createStatement();
			rs = stmt.executeQuery(__dbProperties.getProperty("sql.getAuthors"));
			while (rs.next()) {
				rval.add(new Author(rs.getInt(1), rs.getString(2), rs.getString(3)));
			}
		}
		catch (Exception se) {
			se.printStackTrace();
			return null;
		}
		finally {  // why nest all of these try/finally blocks?
			try {
				if (rs != null) { rs.close(); }
			} catch (Exception e1) { e1.printStackTrace(); }
			finally {
				try {
					if (stmt != null) { stmt.close(); }
				} catch (Exception e2) { e2.printStackTrace(); }
				finally {
					try {
						if (conn != null) { conn.close(); }
					} catch (Exception e3) { e3.printStackTrace(); }
				}
			}
		}

		return rval;
	}

	public int createAuthor(String lname, String fname) {
		if (lname == null || fname == null || lname.length() == 0 || fname.length() == 0) {
			return -1;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(__dbProperties.getProperty("sql.createAuthor"));
			int generatedKey = generateKey(1, 99999);
			stmt.setInt(1, generatedKey);
			stmt.setString(2, lname);
			stmt.setString(3, fname);
			// return stmt.executeUpdate();
			int updatedRows = stmt.executeUpdate();
			if(updatedRows > 0){
				return generatedKey;
			}else{
				return -1;
			}
		} catch (Exception sqe) {
			sqe.printStackTrace();
			return -1;
		} finally {  // why nest all of these try/finally blocks?
			try {
					if (stmt != null) { stmt.close(); }
			} catch (Exception e2) { e2.printStackTrace(); }
			finally {
				try {
					if (conn != null) { conn.close(); }
				} catch (Exception e3) { e3.printStackTrace(); }
			}
		}
	}

	public boolean deleteAuthor(int authorId) {
		Connection conn = null;
		PreparedStatement stmt  = null;
		PreparedStatement stmt2 = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement(__dbProperties.getProperty("sql.deleteAuthor"));
			stmt.setInt(1, authorId);
			stmt.executeUpdate();
			stmt2 = conn.prepareStatement(__dbProperties.getProperty("sql.removeAuthorRefFromBook"));
			stmt2.setInt(1, authorId);
			stmt2.executeUpdate();
			conn.commit();
			return true;
		} catch (Exception sqe) {
			sqe.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		} finally {  // why nest all of these try/finally blocks?
			try {
					if (stmt != null) { stmt.close(); }
					if (stmt2 != null) { stmt2.close(); }
			} catch (Exception e2) { e2.printStackTrace(); }
			finally {
				try {
					if (conn != null) { conn.close(); }
				} catch (Exception e3) { e3.printStackTrace(); }
			}
		}
	}

	@Override
	public Author getAuthor(int id) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Author author = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(__dbProperties.getProperty("sql.getAuthor"));
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				author = new Author(rs.getInt(1), rs.getString(2), rs.getString(3));
			}
		} catch (Exception sqe) {
			sqe.printStackTrace();
		} finally {  // why nest all of these try/finally blocks?
			try {
				rs.close();
				if (stmt != null) { stmt.close(); }
			} catch (Exception e2) { e2.printStackTrace(); }
			finally {
				try {
					if (conn != null) { conn.close(); }
				} catch (Exception e3) { e3.printStackTrace(); }
			}
		}
		return author;
	}

	@Override
	public boolean updateAuthor(Author author) {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(__dbProperties.getProperty("sql.updateAuthor"));
			stmt.setString(1, author.getLastName());
			stmt.setString(2, author.getFirstName());
			stmt.setInt(3, author.getAuthorId());
			return (stmt.executeUpdate() > 0);
		} catch (Exception sqe) {
			sqe.printStackTrace();
			return false;
		} finally {  // why nest all of these try/finally blocks?
			try {
					if (stmt != null) { stmt.close(); }
			} catch (Exception e2) { e2.printStackTrace(); }
			finally {
				try {
					if (conn != null) { conn.close(); }
				} catch (Exception e3) { e3.printStackTrace(); }
			}
		}
	}

	@Override
	public List<Book> getBooks() {
		// TODO Auto-generated method stub
/*
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<Book> rval = new ArrayList<Book>();
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(__dbProperties.getProperty("sql.getBooks"));
			while (rs.next()) { //TODO: may need edit
				rval.add(new Book(rs.getInt(1), rs.getString(2), rs.getString(3)));
			}
		}
		catch (Exception se) {
			se.printStackTrace();
			return null;
		}
		finally {  // why nest all of these try/finally blocks? <-no need to nest into finally if executing anyways.
			try {
				if (rs != null) { rs.close(); }
			} catch (Exception e1) { e1.printStackTrace(); }
			finally {
				try {
					if (stmt != null) { stmt.close(); }
				} catch (Exception e2) { e2.printStackTrace(); }
				finally {
					try {
						if (conn != null) { conn.close(); }
					} catch (Exception e3) { e3.printStackTrace(); }
				}
			}
		}

		return rval;
/**/
		List<Book> val = new ArrayList<Book>();
		val.add(new Book());
		return val;
	}

	@Override
	public Book getBook(int id) {
		// TODO Auto-generated method stub
/*
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Book rval = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(__dbProperties.getProperty("sql.getBook"));
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				rval = new Book(rs.getInt(1), rs.getString(2), rs.getString(3));
			}
		} catch (Exception sqe) {
			sqe.printStackTrace();
		} finally {  // why nest all of these try/finally blocks?
			try {
				rs.close();
				if (stmt != null) { stmt.close(); }
			} catch (Exception e2) { e2.printStackTrace(); }
			finally {
				try {
					if (conn != null) { conn.close(); }
				} catch (Exception e3) { e3.printStackTrace(); }
			}
		}
		return rval;
/**/
		Book val = new Book();
		return val;
	}

	@Override
	public int createBook(String title, int aid, int sid) {
		// TODO Auto-generated method stub
		//TODO: update from author to book data manip.
/*
		if (aid == null || sid == null || aid.length() == 0 || sid.length() == 0) { //
			return -1;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(__dbProperties.getProperty("sql.createBook"));
			int generatedKey = generateKey(1, 99999);
			stmt.setInt(1, generatedKey);
			stmt.setString(2, aid); //
			stmt.setString(3, sid); //
			// return stmt.executeUpdate();
			int updatedRows = stmt.executeUpdate();
			if(updatedRows > 0){
				return generatedKey;
			}else{
				return -1;
			}
		} catch (Exception sqe) {
			sqe.printStackTrace();
			return -1;
		} finally {  // why nest all of these try/finally blocks?
			try {
					if (stmt != null) { stmt.close(); }
			} catch (Exception e2) { e2.printStackTrace(); }
			finally {
				try {
					if (conn != null) { conn.close(); }
				} catch (Exception e3) { e3.printStackTrace(); }
			}
		}
/**/
	return -1;
	}

	@Override
	public Author findAuthorOfBook(int bookId) {
		// TODO Auto-generated method stub
		//TODO: impl this and findBookBySubject
		//return null;
		Author val = new Author(0,"l","f");
		return val;
	}

	@Override
	public List<Subject> getSubjects() {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<Subject> rval = new ArrayList<Subject>();
		try {
			conn = getConnection();

			stmt = conn.createStatement();
			rs = stmt.executeQuery(__dbProperties.getProperty("sql.getSubjects"));
			while (rs.next()) {
				rval.add(new Subject(rs.getInt(1), rs.getString(2), rs.getString(3)));
			}
		}
		catch (Exception se) {
			se.printStackTrace();
			return null;
		}
		finally {  // why nest all of these try/finally blocks?
			try {
				if (rs != null) { rs.close(); }
			} catch (Exception e1) { e1.printStackTrace(); }
			finally {
				try {
					if (stmt != null) { stmt.close(); }
				} catch (Exception e2) { e2.printStackTrace(); }
				finally {
					try {
						if (conn != null) { conn.close(); }
					} catch (Exception e3) { e3.printStackTrace(); }
				}
			}
		}

		return rval;
	}

	@Override
	public Subject getSubject(int id) {
		// TODO Auto-generated method stub
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Subject rval = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(__dbProperties.getProperty("sql.getSubject"));
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				rval = new Subject(rs.getInt(1), rs.getString(2), rs.getString(3));
			}
		} catch (Exception sqe) {
			sqe.printStackTrace();
		} finally {  // why nest all of these try/finally blocks?
			try {
				rs.close();
				if (stmt != null) { stmt.close(); }
			} catch (Exception e2) { e2.printStackTrace(); }
			finally {
				try {
					if (conn != null) { conn.close(); }
				} catch (Exception e3) { e3.printStackTrace(); }
			}
		}
		return rval;
	}

	@Override
	public List<Book> findBooksBySubject(int subjectId) {
		// TODO Auto-generated method stub
		//TODO:see findAuthorOfBook. this impl has List.
//		return null;
		ArrayList<Book> val = new ArrayList<Book>();
		val.add(new Book(0, "inside findBooksBySubject() ias", 1, 2) );
		return val;
	}

	// This class is going to look for a file named rdbm.properties in the classpath
	// to get its initial settings
	static {
		try {
			__dbProperties = new Properties();
			__dbProperties.load(RDBMBooktownServiceImpl.class.getClassLoader().getResourceAsStream("rdbm.properties"));
			__jdbcUrl    = __dbProperties.getProperty("jdbcUrl");
			__jdbcUser   = __dbProperties.getProperty("jdbcUser");
			__jdbcPasswd = __dbProperties.getProperty("jdbcPasswd");
			__jdbcDriver = __dbProperties.getProperty("jdbcDriver");
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
		}
	}
}
