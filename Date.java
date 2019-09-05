import java.io.Serializable;

/*the following class represents dates.*/
class Date implements Serializable {
    private final Integer year;
    private final Integer month;
    private final Integer day;

    /*date constructor, receives day, month and year. throws exception on invalid dates*/
    Date(Integer dd, Integer mm, Integer yy) /*throws InvalidDateInit*/ {
        /*if (!(dd > 0 && dd <= 31)) throw new InvalidDateInit("invalid day") ;*/
        /*if (!(mm > 0 && mm <= 12)) throw new InvalidDateInit("invalid day") ;*/
        this.day = dd;
        this.month = mm;
        this.year = yy;
    }

    /*invalid date initialization values exception*/
    private class InvalidDateInit extends Exception {
        InvalidDateInit(String msg) {
            super(msg);
        }
    }

    /*equals override, checked via comparison on instance variables*/
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; /*return true if compared to itself*/
        if (obj instanceof Date) /*if of type Date, return instance variables comparison*/
            return this.day.equals(((Date) obj).day) && this.month.equals(((Date) obj).month) && this.year.equals(((Date) obj).year);
        return false; /*not Date*/
    }

    /*return string representation of the given date. fields separated by a back slash*/
    @Override
    public String toString() {
        return day + "/" + month + "/" + year;
    }

    /*return hashcode of the string representation, two dates has the same string iff there are
     * identical. there for the function returns the hashcode of the date's string representation*/
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
