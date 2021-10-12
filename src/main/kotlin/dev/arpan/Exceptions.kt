package dev.arpan

/**
 * Validation of input parameters failed.
 */
class ValidationException(msg: String) : Exception(msg)

/**
 * A resource could not be found.
 */
class NotFoundException(msg: String) : Exception(msg)

/**
 * User doesn't have access to a resource.
 */
class IllegalAccessException(msg: String) : SecurityException(msg)

/**
 * Operation failed for some reason e.g. because the db or some external service wasn't available.
 */
class ServiceUnavailable(msg: String) : Exception(msg)
