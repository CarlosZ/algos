package org.czuniga.algos.topcoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

/**
 * Problem Statement
 *
 * Class Name: Prerequisites
 * Mathod Name: orderClasses
 * Parameters: String[]
 * Returns: String[]
 *
 * You are a student at a college with the most unbelievably complex prerequisite
 * structure ever. To help you schedule your classes, you decided to put together
 * a program that returns the order in which the classes should be taken.
 *
 * Implement a class Prerequisites which contains a method orderClasses.  The
 * method takes a String[] that contains the classes you must take and returns a
 * String[] of classes in the order the classes should be taken so that all
 * prerequisites are met.
 *
 * String[] elements will be of the form (and TopCoder will ensure this):
 * "CLASS: PRE1 PRE2 ..." where PRE1 and PRE2 are prerequisites of CLASS.  CLASS,
 * PRE1, PRE2, ... consist of a department name (3 or 4 capital letters, A-Z
 * inclusive) followed by a class number (an integer between 100 and 999,
 * inclusive).  The department name should be immediately followed by the class
 * number with no additional characters, numbers or spaces (i.e. MATH217).  It is
 * not necessary for a class to have prerequisites.  In such a case, the colon is
 * the last character in the String.
 *
 * You can only take one class at a time, therefore, use the following rules to
 * determine which class to take :
 * 1) Any prerequisite class(es) listed for a class must be taken before the class
 * can be taken.
 * 2) If multiple classes can be taken at the same time, you take the one with the
 * lowest number first, regardless of department.
 * 3) If multiple classes with the same number can be taken at the same time, you
 * take the department name which comes first in alphabetical order.
 * 4) If the inputted course schedule has errors, return a String[] of length 0.
 * There is an error if it is impossible to return the classes in an order such
 * that all prerequisites are met, or if a prerequisite is a course that does not
 * have its own entry in the inputted String[].
 *
 * Examples of valid input Strings are:
 * "CSE111: CSE110 MATH101"
 * "CSE110:"
 *
 * Examples of invalid input Strings are:
 * "CS117:" (department name must consist of 3 - 4 capital letters, inclusive)
 * "cs117:" (department name must consist of 3 - 4 capital letters, inclusive)
 * "CS9E11:" (department name must be letters only)
 * "CSE110: " (no trailing spaces allowed)
 * "CSE110: CSE101 " (no trailing spaces allowed)
 * "MATH211: MAM2222" (class number to large)
 * "MATH211: MAM22" (class number to small)
 * "ENGIN517: MATH211" (department name to large)
 *
 * Here is the method signature (be sure your method is public):
 * String[] orderClasses(String[] classSchedule);
 *
 * TopCoder will make sure classSchedule contains between 1 and 20 Strings,
 * inclusive, all of the form above.  The Strings will have between 1 and 50
 * characters, inclusive.  TopCoder will check that the syntax of the Strings are
 * correct: The Strings will contain a valid class name, followed by a colon,
 * possibly followed by a series of unique prerequisite classes separated by
 * single spaces.  Also, TopCoder will ensure that each class has at most one
 * entry in the String[].
 *
 * Examples:
 * If classSchedule={
 * "CSE121: CSE110",
 * "CSE110:",
 * "MATH122:",
 * }
 * The method should return: {"CSE110","CSE121","MATH122"}
 *
 * If classSchedule={
 * "ENGL111: ENGL110",
 * "ENGL110: ENGL111"
 * }
 * The method should return: {}
 *
 * If classSchedule=[
 * "ENGL111: ENGL110"
 * }
 * The method should return: {}
 *
 * If classSchedule={
 * "CSE258: CSE244 CSE243 INTR100"
 * "CSE221: CSE254 INTR100"
 * "CSE254: CSE111 MATH210 INTR100"
 * "CSE244: CSE243 MATH210 INTR100"
 * "MATH210: INTR100"
 * "CSE101: INTR100"
 * "CSE111: INTR100"
 * "ECE201: CSE111 INTR100"
 * "ECE111: INTR100"
 * "CSE243: CSE254"
 * "INTR100:"
 * }
 * The method should return:
 * {"INTR100","CSE101","CSE111","ECE111","ECE201","MATH210","CSE254","CSE221","CSE2
 * 43","CSE244","CSE258"}
 * Definition
 *     
 * Class:
 * Prerequisites
 * Method:
 * orderClasses
 * Parameters:
 * String[]
 * Returns:
 * String[]
 * Method signature:
 * String[] orderClasses(String[] param0)
 * (be sure your method is public)
 *
 * Taken from TopCoder.com
 */
public class Prerequisites {

  private boolean debug;

  public Prerequisites(boolean debug) {
    this.debug = debug;
  }

  public String[] orderClasses(String[] schedule) {
    List<Course> courses = toCourses(schedule);

    int COURSE_SIZE = courses.size();
    boolean courseMatrix[][] = new boolean[COURSE_SIZE][COURSE_SIZE];
    for (Course course : courses) {
      for (Course otherCourse : courses) {
        if (otherCourse != course && otherCourse.hasRequisite(course)) {
          courseMatrix[otherCourse.index][course.index] = true; // there's a requisite
        }
      }
    }

    print(courseMatrix, courses);

    List<Integer> noIncoming = findEdgesWithNoIncoming(courseMatrix);
    Queue<Course> s = new PriorityQueue<Course>();
    for (Integer index : noIncoming) {
      s.add(courses.get(index));
    }

    logln("No incoming: " + s);

    List<String> ordered = new ArrayList<String>();

    while (!s.isEmpty()) {
      int n = s.poll().index;
      ordered.add(courses.get(n).toString());

      for (int m = 0; m < courseMatrix.length; m++) {
        if (courseMatrix[m][n]) {
          courseMatrix[m][n] = false;
          if (!hasIncoming(courseMatrix, m)) {
            s.add(courses.get(m));
          }
        }
      }
    }

    logln("ordered = " + ordered);

    return ordered.toArray(new String[ordered.size()]);
  }

  private boolean hasIncoming(boolean[][] courseMatrix, int x) {
    for (int y = 0; y < courseMatrix.length; y++) {
      if (courseMatrix[x][y]) {
        return true;
      }
    }
    return false;
  }

  private List<Integer> findEdgesWithNoIncoming(boolean[][] courseMatrix) {
    List<Integer> noIncoming = new ArrayList<Integer>();
    for (int x = 0; x < courseMatrix.length; x++) {
      boolean hasIncoming = false;
      for (int y = 0; y < courseMatrix.length; y++) {
        if (courseMatrix[x][y]) {
          hasIncoming = true;
          break;
        }
      }
      if (!hasIncoming) {
        noIncoming.add(x);
      }
    }

    return noIncoming;
  }

  private void print(boolean[][] courseMatrix, List<Course> courses) {
    boolean first = true;
    for (Course course : courses) {
      if (first) {
        log("         ");
        first = false;
      }
      log(course + "  ");
    }
    logln("");
    for (int y = 0; y < courseMatrix.length; y++) {
      log(courses.get(y) + "  ");
      for (int x = 0; x < courseMatrix.length; x++) {
        log(courseMatrix[x][y] ? "    Y    " : "         ");
      }
      logln("");
      logln("");
    }
  }

  private List<Course> toCourses(String[] schedule) {
    List<Course> courses = new ArrayList<Course>();
    for (String line : schedule) {
      courses.add(new Course(line));
    }
    Collections.sort(courses);
    int index = 0;
    for (Course course : courses) {
      course.index = index++;
    }
    for (Course course : courses) {
      for (Course requisite : course.requisites) {
        if (!courses.contains(requisite)) {
          return new ArrayList<Course>();
        }
      }
    }
    return courses;
  }

  private void logln(String message) {
    log(message + "\n");
  }

  private void log(String message) {
    if (debug) {
      out.print(message);
    }
  }

  private static class Course implements Comparable<Course> {

    private static final Pattern VALID_COURSE_NAME = Pattern.compile("([A-Z]{3,4})(\\d{3})(:.*)?");

    String department;
    Integer number;
    Set<Course> requisites = new HashSet<Course>();
    Integer index = 0;

    public Course(String courseName) {
      Matcher matcher = VALID_COURSE_NAME.matcher(courseName);
      if (!matcher.matches()) {
        throw new IllegalArgumentException("Invalid course " + courseName);
      }
      department = matcher.group(1);
      number = Integer.valueOf(matcher.group(2));
      if (number < 100 || number > 999) {
        throw new IllegalArgumentException("Invalid course " + courseName);
      }
      String reqsString = matcher.group(3);
      if (reqsString != null && reqsString.length() > 0) {
        String reqsList = reqsString.substring(1).trim();
        if (reqsList.length() > 0) {
          String[] reqs = reqsList.split(" ");
          for (String req : reqs) {
            requisites.add(new Course(req));
          }
        }
      }
    }

    public boolean hasRequisite(Course course) {
      return requisites.contains(course);
    }

    @Override
    public String toString() {
      return department + String.valueOf(number);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      Course course = (Course) o;

      if (!department.equals(course.department)) {
        return false;
      }
      if (!number.equals(course.number)) {
        return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      int result = department.hashCode();
      result = 31 * result + number.hashCode();
      return result;
    }

    public int compareTo(Course course) {
      int numberComp = number.compareTo(course.number);
      if (numberComp == 0) {
        return department.compareTo(course.department);
      } else {
        return numberComp;
      }
    }
  }

  public static void main(String[] args) {

    Prerequisites prerequisites = new Prerequisites(false);

    String[] schedule0 = {
        "CSE258: CSE244 CSE243 INTR100",
        "CSE221: CSE254 INTR100",
        "CSE254: CSE111 MATH210 INTR100",
        "CSE244: CSE243 MATH210 INTR100",
        "MATH210: INTR100",
        "CSE101: INTR100",
        "CSE111: INTR100",
        "ECE201: CSE111 INTR100",
        "ECE111: INTR100",
        "CSE243: CSE254",
        "INTR100:"
    };

    assertArrayEquals(prerequisites.orderClasses(schedule0), new String[]{"INTR100", "CSE101", "CSE111", "ECE111", "ECE201",
        "MATH210", "CSE254", "CSE221", "CSE243", "CSE244", "CSE258"});

    String[] schedule1 = {
        "ENGL111: ENGL110",
        "ENGL110: ENGL111"
    };

    assertArrayEquals(prerequisites.orderClasses(schedule1), new String[]{});

    String[] schedule2 = {
        "ENGL111: ENGL110"
    };

    assertArrayEquals(prerequisites.orderClasses(schedule2), new String[]{});

    String[] schedule3 = {
        "BUBB100:",
        "DUB101:",
        "ZUB100:",
        "ZUBB140:",
        "ZUBA150:",
        "JUM104:"
    };

    assertArrayEquals(prerequisites.orderClasses(schedule3),
                      new String[]{"BUBB100", "ZUB100", "DUB101", "JUM104", "ZUBB140", "ZUBA150"});

    String[] schedule4 = {
        "CSE121: CSE110",
        "CSE110:",
        "MATH122:"
    };

    assertArrayEquals(prerequisites.orderClasses(schedule4), new String[]{"CSE110", "CSE121", "MATH122"});
  }

  private static void assertArrayEquals(String[] actual, String[] expected) {
    if (!Arrays.equals(actual, expected)) {
      throw new AssertionError("\nExpected: " + Arrays.toString(expected) + "\n" + "Actual:   " + Arrays.toString(actual));
    }
  }
}
