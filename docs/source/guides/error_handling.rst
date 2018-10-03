.. _error-handling:

.. Defined for inline java code highlighting
.. role:: java(code)
   :language: java

.. toctree::
   :name: error_handling
   :includehidden:

Error Handling
==============

Beadledom has implemented a standard behavior for handling errors in a RESTful service that follows
the `Google API <https://developers.google.com/drive/v2/web/handle-errors>`_ structured error
message format. This format allows exceptions to be mapped to responses in JSON format with only
the information that is necessary or desired for a consuming application.

Two models exist for the purpose of allowing easily-constructed JSON responses:

- `JsonError <https://github.com/cerner/beadledom/blob/master/json-common/src/main/java/com.cerner.beadledom.json.common/model/JsonError.java>`_
  - A class that can be used to build a JSON response.
- `ErrorDetail <https://github.com/cerner/beadledom/blob/master/json-common/src/main/java/com.cerner.beadledom.json.common/model/ErrorDetail.java>`_
  - A class that can be used to add additional error details to the JSON response.

These models are serializable objects that can be set as the response's entity body.

Example Response:

.. code-block:: json

  {
    "errors": [
      {
        "domain": "global",
        "reason": "notFound",
        "message": "Not Found"
      }
    ],
    "code": 404,
    "message": "Not Found"
  }

Example Response (without additional errors information):

.. code-block:: json

  {
    "code": 404,
    "message": "Not Found"
  }


Behavior
________

Without the use of an `ExceptionMapper <http://docs.oracle.com/javaee/6/api/javax/ws/rs/ext/ExceptionMapper.html>`_,
an unhandled exception thrown from a resource endpoint will return an HTTP response in plain text
or HTML that may contain stack traces or other system-specific information that should not be
displayed to the end-user.

Beadledom addresses this concern by implementing exception mappers. By default, Beadledom comes
packaged with five exception mappers that handle this functionality behind the scenes:

- `WebApplicationExceptionMapper <https://github.com/cerner/beadledom/blob/master/jaxrs/src/main/java/com/cerner/beadledom/jaxrs/exceptionmapping/WebApplicationExceptionMapper.java>`_
  - An exception mapper for exceptions in the WebApplicationException family.
- `FailureExceptionMapper <https://github.com/cerner/beadledom/blob/master/resteasy/src/main/java/com/cerner/beadledom/resteasy/exceptionmapping/FailureExceptionMapper.java>`_
  - An exception mapper for exceptions in the Failure family which encompasses internal exceptions raised by Resteasy.
- `ThrowableExceptionMapper <https://github.com/cerner/beadledom/blob/master/jaxrs/src/main/java/com/cerner/beadledom/jaxrs/exceptionmapping/ThrowableExceptionMapper.java>`_
  - A catch-all exception mapper for all other unhandled exceptions.
- `JsonMappingExceptionMapper <https://github.com/cerner/beadledom/blob/master/jaxrs/src/main/java/com/cerner/beadledom/jaxrs/exceptionmapping/JsonMappingExceptionMapper.java>`_
  - An exception mapper for Jackson JsonMappingExceptions.
- `JsonParseExceptionMapper <https://github.com/cerner/beadledom/blob/master/jaxrs/src/main/java/com/cerner/beadledom/jaxrs/exceptionmapping/JsonParseExceptionMapper.java>`_
  - An exception mapper for Jackson JsonParseExceptions.

Each of these ExceptionMapper classes has a distinct role in a RESTful service.

The ``WebApplicationExceptionMapper`` is intended to handle all exceptions thrown from a service
that extend from the ``WebApplicationException`` hierarchy. This includes exceptions such as
``BadRequestException``, ``NotFoundException``, custom exceptions extending from
``WebApplicationException``, etc. ``WebApplicationExceptionMapper`` treats the exception as-is but
sanitizes the exception's response, status, and message and formats it as JSON before sending back
a response. The status will be propagated through to the response. The response message will be
derived from the resulting status code or the status code's family if the status is unrecognized.

The ``FailureExceptionMapper`` is meant to be used with projects using Resteasy and handles all
exceptions thrown internally by Resteasy that extend from the ``Failure`` hierarchy. Resteasy has
`built-in internally-thrown exceptions <https://docs.jboss.org/resteasy/docs/2.2.1.GA/userguide/html/ExceptionHandling.html#builtinException>`_
that need to be handled and reformatted before sending back a response. If the exception's response
is not null, its status code will be used as the status of the response. If the exception's response
is null, then the error code on the ``Failure`` exception will be used as the status of the
response if it is in the valid status code range (100-599). If neither of these are true, the
status is mapped to an Internal Server Error (500 status code). The response message will be
derived from the resulting status code or the status code's family if the status is unrecognized.

``ThrowableExceptionMapper`` is the exception mapper that will behave as a catch-all for all
exceptions that go unhandled. An unhandled exception could match any of the following scenarios:

- The exception was a checked exception that was not handled by try/catch blocks.
- The exception was a checked exception that was missing an exception mapper or the exception mapper was implemented incorrectly.
- The exception was an unchecked exception.

Any exception thrown in a resource matching one of the scenarios mentioned above will be treated as
an Internal Server Error (500 status code).

Keep in mind that an unhandled exception will be caught by the exception mapper that is most
specific to the exception. If there is not an exception mapper for the specific exception, the next
closest exception mapper in the exception's hierarchy will handle it. For example, if a
`NotFoundException <http://docs.oracle.com/javaee/7/api/javax/ws/rs/NotFoundException.html>`_
(which extends from ``WebApplicationException``) is thrown, the ``WebApplicationExceptionMapper``
will handle it, rather than the ``ThrowableExceptionMapper``.

Implementation
______________

Each of the exception mappers mentioned above are readily available for consumption. They
are automatically added to a project that have installed either the ``BeadledomModule``
(``WebApplicationExceptionMapper`` and ``ThrowableExceptionMapper`` only) or the ``ResteasyModule``
(``WebApplicationExceptionMapper``, ``FailureExceptionMapper``, and ``ThrowableExceptionMapper``).

NOTE: If your service is able to throw custom exceptions, it is suggested that a custom
`ExceptionMapper <https://docs.jboss.org/resteasy/docs/3.1.4.Final/userguide/html/ExceptionHandling.html#ExceptionMappers>`_
is added to your project as well. This will allow you to customize the response that is returned;
otherwise, there is a chance that the custom exception may be handled by the
``ThrowableExceptionMapper`` and return a 500 response, which may be undesired. While creating
custom exception mappers, stay consistent with the error handling format by using the ``JsonError``
and ``ErrorDetail`` models to create the response entity.
