package com.pzpg.ogr.api.request

import java.lang.Exception

/**
 * Opens Exception class to throw this when something is wrong with requests to the server
 * @param message a message to show, inherited from Exception standard class
 *
 * @author Władysław Jakołcewicz
 */
open class RequestServerException(message: String?): Exception(message)

/**
 * BadRequestException class to throw this when request message is invalid for server
 * Has thrown when code status of the request is 400
 * @param message a message to show, inherited from Exception standard class
 *
 * @author Władysław Jakołcewicz
 */
class BadRequestException(message: String?): RequestServerException(message)

/**
 * UnauthorizedException class to throw this when user has not authorized
 * Has thrown when code status of the request is 401
 * @param message a message to show, inherited from Exception standard class
 *
 * @author Władysław Jakołcewicz
 */
class UnauthorizedException(message: String?): RequestServerException(message)

/**
 * BadRequestException class to throw this when method of request is not allowed
 * Has thrown when code status of the request is 405
 * @param message a message to show, inherited from Exception standard class
 *
 * @author Władysław Jakołcewicz
 */
class NotAllowedMethodException(message: String?): RequestServerException(message)

/**
 * Has thrown when code status of the request is -1
 * @param message a message to show, inherited from Exception standard class
 *
 * @author Władysław Jakołcewicz
 */
class TimeOutException(message: String?): RequestServerException(message)