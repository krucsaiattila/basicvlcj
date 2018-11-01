/* 
 * Copyright 2012 Fredy Wijaya
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package srt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class to store SRT information.
 * 
 * @author fredy
 */
public class SRT implements Comparable<SRT> {
    public final int number;
    public final Date startTime;
    public final Date endTime;
    public List<String> words = new ArrayList<>();
    public long startInMilliseconds;
    public long endInMilliseconds;
    
    /**
     * Creates a new instance of SRT.
     * 
     * @param number the subtitle number
     * @param startTime the start time
     * @param endTime the end time
     * @param lines the subtitle text
     */
    public SRT(int number, Date startTime, Date endTime, String... lines) {
        this.number = number;
        this.startTime = startTime;
        this.endTime = endTime;
        for (String s: Arrays.asList(lines)){
            String [] wordsArray = s.split("\\s+");
            for (int i = 0; i < wordsArray.length; i++) {
                //Check for non letter characters, and remove them
                //wordsArray[i] = wordsArray[i].replaceAll("[^\\w]", "");
            }
            words = Stream.of(words, Arrays.asList(wordsArray)).flatMap(Collection::stream).collect(Collectors.toList());
        }
        if(startTime != null && endTime != null) {
            ZonedDateTime dateInInstant = startTime.toInstant().atZone(ZoneId.systemDefault());
            startInMilliseconds = dateInInstant.getNano() / 1000000 + dateInInstant.getSecond() * 1000 + dateInInstant.getMinute() * 60000 + dateInInstant.getHour() * 3600000;
            dateInInstant = endTime.toInstant().atZone(ZoneId.systemDefault());
            endInMilliseconds = dateInInstant.getNano() / 1000000 + dateInInstant.getSecond() * 1000 + dateInInstant.getMinute() * 60000 + dateInInstant.getHour() * 3600000;
        }
    }
    
    /**
     * Creates a new instance of SRT.
     * 
     * @param number the subtitle number
     * @param startTime the start time
     * @param endTime the end time
     * @param lines the subtitle text
     */
    public SRT(int number, Date startTime, Date endTime, List<String> lines) {
        this.number = number;
        this.startTime = startTime;
        this.endTime = endTime;
        for (String s: lines){
            String [] wordsArray = s.split("\\s+");
            for (int i = 0; i < wordsArray.length; i++) {
                //Check for non letter characters, and remove them
                //wordsArray[i] = wordsArray[i].replaceAll("[^\\w]", "");
            }
            words = Stream.of(words, Arrays.asList(wordsArray)).flatMap(Collection::stream).collect(Collectors.toList());
        }
        if(startTime != null && endTime != null) {
            ZonedDateTime dateInInstant = startTime.toInstant().atZone(ZoneId.systemDefault());
            startInMilliseconds = dateInInstant.getNano() / 1000000 + dateInInstant.getSecond() * 1000 + dateInInstant.getMinute() * 60000 + dateInInstant.getHour() * 3600000;
            dateInInstant = endTime.toInstant().atZone(ZoneId.systemDefault());
            endInMilliseconds = dateInInstant.getNano() / 1000000 + dateInInstant.getSecond() * 1000 + dateInInstant.getMinute() * 60000 + dateInInstant.getHour() * 3600000;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (number ^ (number >>> 32));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SRT other = (SRT) obj;
        if (number != other.number)
            return false;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(SRT o) {
        return new Integer(number).compareTo(o.number);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SRT [number=").append(number).append(", startTime=")
            .append(startTime).append(", endTime=").append(endTime).append(", text=")
            .append(words).append("]");
        return builder.toString();
    }
}
