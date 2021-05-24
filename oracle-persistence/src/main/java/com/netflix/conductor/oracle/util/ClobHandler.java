package com.netflix.conductor.oracle.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClobHandler {
	
	private static Logger log = LoggerFactory.getLogger(ClobHandler.class.getSimpleName());

	public static String clobToString(Clob data)
	{
	    final StringBuilder sb = new StringBuilder();

	    try
	    {
	        final Reader         reader = data.getCharacterStream();
	        final BufferedReader br     = new BufferedReader(reader);

	        int b;
	        while(-1 != (b = br.read()))
	        {
	            sb.append((char)b);
	        }

	        br.close();
	    }
	    catch (SQLException e)
	    {
	        log.error("SQL. Could not convert CLOB to string",e);
	        return e.toString();
	    }
	    catch (IOException e)
	    {
	        log.error("IO. Could not convert CLOB to string",e);
	        return e.toString();
	    }

	    return sb.toString();
	}
}
