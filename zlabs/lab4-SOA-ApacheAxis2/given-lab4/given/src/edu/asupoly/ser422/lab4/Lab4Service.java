package edu.asupoly.ser422.lab4;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public  class Lab4Service {
	// These are the possible queries
	private static String queryAll = "SELECT grade FROM Enrolled";
	private static String queryYear = "SELECT grade from Enrolled JOIN Student ON (Enrolled.sid=Student.id) WHERE year=";
	private static String querySubject = "SELECT grade from Enrolled JOIN Course ON (Enrolled.crsid=Course.id) WHERE subject='";
	private static String queryYearSubject = "SELECT grade from (Student JOIN Enrolled ON (Student.id=Enrolled.sid)) JOIN Course ON (Enrolled.crsid=Course.id) WHERE year=";

	private String __jdbcUrl    = null;
	private String __jdbcUser   = null;
	private String __jdbcPasswd = null;
	private String __jdbcDriver = null;

	// Singleton pattern
	private static Lab4Service __theService = null;

	public static final Lab4Service getService() throws Exception {
		if (__theService == null) {
			__theService = new Lab4Service();
		}
		return __theService;
	}

	private Lab4Service() throws Exception {
		Properties props = new Properties();
		try {
			InputStream propFile = this.getClass().getClassLoader().getResourceAsStream("lab4db.properties");
			props.load(propFile);
			propFile.close();
		}
		catch (IOException ie) {
			ie.printStackTrace();
			throw new Exception("Could not open property file");
		}

		__jdbcUrl    = props.getProperty("jdbc.url");
		__jdbcUser   = props.getProperty("jdbc.user");
		__jdbcPasswd = props.getProperty("jdbc.passwd");
		__jdbcDriver = props.getProperty("jdbc.driver");
		try {
			Class.forName(__jdbcDriver); // ensure the driver is loaded
		}
		catch (ClassNotFoundException cnfe) {
			System.out.println("*** Cannot find the JDBC driver");
			cnfe.printStackTrace();
			throw new Exception("Cannot initialize service from property file");
		}
	}

	private String selectQuery(String year, String subject) {
		// Polymorphism sure could do this more elegantly
		if (year != null && subject != null) {
			return queryYearSubject + year + " AND subject='" + subject + "'";
		}
		if (year != null) {
			return queryYear + year;
		}
		if (subject != null) {
			return querySubject + subject + "'";
		}
		return queryAll;
	}

	// This is what you need to implement in a subclass!
	public double calculateGrade(String year, String subject) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		double grade = 0.0;
		try {
			// Create the connection anew every time
			conn = DriverManager.getConnection(__jdbcUrl, __jdbcUser, __jdbcPasswd);

			stmt = conn.createStatement();
			String query = selectQuery(year, subject);

			rs = stmt.executeQuery(query);
			int count = 0;
			double gradesum = -1.0;
			while (rs.next()) {
				gradesum += rs.getDouble(1);
				count++;
			}
			if (count > 0) {
				grade = gradesum / count;
			}
		}
		catch (SQLException se) {
			System.out.println("*** Uh-oh! Database Exception");
			se.printStackTrace();
		}
		catch (Exception e) {
			System.out.println("*** Some other exception was thrown");
			e.printStackTrace();
		}
		finally {  // why nest all of these try/finally blocks?
			try {
				if (rs != null) { rs.close(); }
			} catch (Throwable t1) {
				t1.printStackTrace();
			}
			try {
				if (stmt != null) { stmt.close(); }
			} catch (Throwable t2) {
				t2.printStackTrace();
			}
			try {
				if (conn != null) { conn.close(); }
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
		}
		// Note that error cases will return this as well which is not good
		return grade;
	}

	public final String mapToLetterGrade(double grade) {
		if (grade >= 98.0) return "A+";
		if (grade >= 93.0) return "A";
		if (grade >= 90.0) return "A-";
		if (grade >= 88.0) return "B+";
		if (grade >= 83.0) return "B";
		if (grade >= 80.0) return "B-";
		if (grade >= 77.0) return "C+";
		if (grade >= 70.0) return "C";
		if (grade >= 60.0) return "D";
		if (grade < 0.0) return "I";
		return "E";
	}
}
