/*
 * Copyright (C) 2011 - 2012, psanker and contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this list of 
 *   conditions and the following 
 * * Redistributions in binary form must reproduce the above copyright notice, this list of 
 *   conditions and the following disclaimer in the documentation and/or other materials 
 *   provided with the distribution.
 * * Neither the name of Overcaffeinated Development nor the names of its contributors may be 
 *   used to endorse or promote products derived from this software without specific prior 
 *   written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.thevoxelbox.isay;

import com.rosaloves.bitlyj.Url;
import static com.rosaloves.bitlyj.Bitly.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MessageFormattingServices {
    
    private static final String urlFormat = "((http|ftp|https)\\:\\/\\/)(w+?\\.)?([a-zA-Z0-9\\-\\._?%&=~#])+\\.([a-zA-Z]){2,4}(\\.([a-zA-Z]){2,2})?(\\:\\d)?(\\/[a-zA-Z0-9\\-\\.\\:_?%&=~#]*)*";
    private static final String bitLyFormat = "(http|https)\\:\\/\\/(bit)\\.(ly)\\/[a-zA-Z0-9]{6}";
    
    // --- URL Services ---
    public static boolean isURL(String in)
    {
        Pattern pattern = Pattern.compile(urlFormat);
        Matcher match = pattern.matcher(in);
        
        return (match.find() && (match.group(0).length() == in.length()));
    }
    
    public static boolean isBitLy(String in)
    {
        Pattern pattern = Pattern.compile(bitLyFormat);
        Matcher match = pattern.matcher(in);
        
        return match.find();
    }
    
    public static boolean containsURLs(String in)
    {
        Pattern pattern = Pattern.compile(urlFormat);
        Matcher match = pattern.matcher(in);
        return match.find();
    }
    
    public static String shortenURLs(String in)
    {
        Pattern urlPattern = Pattern.compile(urlFormat);
        Matcher match = urlPattern.matcher(in);
        
        while (match.find()) {
            String nextURL = match.group();
            
            if (isBitLy(nextURL)) {
                continue;
            }
            
            String shortened = getShortenedURL(nextURL);
            
            if (!("".equals(shortened))) {
                in = in.replace(nextURL, shortened);
            }
        }
        
        return in;
    }
    
    private static String getShortenedURL(final String longURL)
    {
        String shortURL = "";
//        String line;
//        
//        HttpURLConnection connection = null;
//        BufferedReader reader = null;
//        
//        try {
//            URL url = new URL("https://www.googleapis.com/urlshortener/v1/url");
//            connection = (HttpURLConnection) url.openConnection();
//            
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setDoOutput(true);
//            connection.connect();
//            
//            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
//            writer.write("{\"longUrl\": \"" + longURL + "\"}");
//            writer.flush();
//            
//            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            Pattern shortenedURLPattern = Pattern.compile("http\\:\\/\\/goo\\.gl\\/\\w+");
//            
//            while ((line = reader.readLine()) != null) {
//                Matcher match = shortenedURLPattern.matcher(line);
//                
//                if (match.find(0)) {
//                    shortURL = match.group(0);
//                }
//            }
//            
//            return shortURL;
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            if (reader != null || connection != null) {
//                try {
//                    connection.disconnect();
//                    reader.close();
//                } catch (IOException ex) {
//                    // Will log if needed
//                }
//            }
//        }
        
        Url url = as("psanker", "R_c434cf238771a5267d1f100d82ba7433").call(shorten(longURL));
        shortURL = url.getShortUrl();
        
        if (shortURL != null)
            return shortURL;
        
        return "";
    }
}
