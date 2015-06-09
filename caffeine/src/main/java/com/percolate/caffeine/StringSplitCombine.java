package com.percolate.caffeine;

/**
 * Created by sensemilla on 25.02.15.
 */
    import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StringSplitCombine {
    private static String eventReminderdate;
    private static String dateforsplit;
    private static int yr;
    private static int mr;
    private static int dr;
    private static int year;
    private static int month;
    private static int day;

    public static ArrayList<String> spliteByToken(String src, String token) {
            String[] array = src.split(token);
            ArrayList<String> ret = new ArrayList<String>();
            for (String temp : array) {
                if (temp != "") {
                    ret.add(temp);
                }
            }
            return ret;
        }

        public static String combineByToken(ArrayList<String> list, String token) {
            StringBuffer buf = new StringBuffer();
            for (String temp : list) {
                buf.append(token).append(temp);
            }
            if (buf.length() != 0) {
                buf.deleteCharAt(0);// will refactory soon here
            }
            return buf.toString();
        }

    public static String stripNonDigits(
            final CharSequence input /* inspired by seh's comment */){
        final StringBuilder sb = new StringBuilder(
                input.length() /* also inspired by seh's comment */);
        for(int i = 0; i < input.length(); i++){
            final char c = input.charAt(i);
            if(c > 47 && c < 58){
                sb.append(c);
            }
        }
        return sb.toString();
    }


    public static String truncate(String src, int length, String end) {
        if (src == null) {
            return null;
        }

        if (src.length() > length) {
            return src.substring(0, length) + end;
        } else {
            return src;
        }
    }


    /************************************************
     * utility part
     */
    private static final String DATE_TIME_FORMAT = "yyyy MMM dd, HH:mm:ss";
    public static String getDateTimeStr(int p_delay_min) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        if (p_delay_min == 0) {
            return sdf.format(cal.getTime());
        } else {
            Date l_time = cal.getTime();
            l_time.setMinutes(l_time.getMinutes() + p_delay_min);
            return sdf.format(l_time);
        }
    }
    public static String getDateTimeStr(String p_time_in_millis) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        Date l_time = new Date(Long.parseLong(p_time_in_millis));
        return sdf.format(l_time);
    }


    public static String formatHoursAndMinutes(int totalMinutes) {
        String minutes = Integer.toString(totalMinutes % 60);
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        return (totalMinutes / 60) + ":" + minutes;
    }

    public static String removeCharAt(String s, int pos) {
        StringBuffer buf = new StringBuffer( s.length() - 1 );
        buf.append( s.substring(0,pos) ).append( s.substring(pos+1) );
        return buf.toString();
    }

    //todo
    /*public static EventDateTime getEventTime(int sec, int min, int hour, int monthDay, int month, int year){
        //¿ù(month)Àº -1ÇØ¼­ ³Ö¾îÁÖ°í, ÀÏ(monthDay)Àº +1ÇØ¼­ ³Ö¾îÁà¾ßÇÔ
        Time makeTime = new Time();
        makeTime.set(sec, min, hour, monthDay+1, month-1, year);
        makeTime.normalize(true);
        DateTime makeDate = new DateTime(makeTime.toMillis(false));
        EventDateTime makeDateTime = new EventDateTime();
        makeDateTime.setDateTime(makeDate);
        makeDateTime.setTimeZone("Europe/Amsterdam");
        return makeDateTime;
    }*/
    public static String doreplace(String formod) {
        String newString = formod.replace("%3A", ":");
        newString = newString.replace("%2F", "/");
        newString = newString.replace("%3F", "?");
        newString = newString.replace("%3D", "=");
        newString = newString.replace("%26", "&");
        newString = newString.replace("%252", "%2");
        return newString;
    }

    public static String getEventReminderDate(int inyear, int inmonthOfYear, int indayOfMonth) {
       // todo dateforsplit = EventDialogBuilder.startdate.getText().toString();
        ArrayList<String> startdateorig = StringSplitCombine.spliteByToken(dateforsplit, "-");
        int sy, sm, sd;
        sy = Integer.parseInt(startdateorig.get(0));
        sm = Integer.parseInt(startdateorig.get(1));
        sd = Integer.parseInt(startdateorig.get(2));
        int y, m, d;
        y = inyear;
        m = inmonthOfYear;
        d = indayOfMonth;

        yr = sy - y;
        mr = sm - m;
        dr = sd - d;
///////////////////////////////////////////////////////////////////////////////////////////////////TODO: below value of selected ReminderDate
        year = inyear;
        month = inmonthOfYear;
        day = indayOfMonth;

        String month = null;
        String day = null;
        if (inmonthOfYear <= 10) {
            month = "0" + inmonthOfYear;
        } else {
            month = String.valueOf(inmonthOfYear);
        }
        if (indayOfMonth <= 10) {
            day = "0" + indayOfMonth;
        } else {
            day = String.valueOf(indayOfMonth);
        }

        eventReminderdate =  year + "-" + month + "-" + day;
        return eventReminderdate;
    }



    
    }





