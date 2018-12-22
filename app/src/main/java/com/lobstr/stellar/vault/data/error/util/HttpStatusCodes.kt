package com.lobstr.stellar.vault.data.error.util

class HttpStatusCodes {

    /**
     * 2XX: generally "OK"
     * 3XX: relocation/redirect
     * 4XX: client error
     * 5XX: server error
     */
    
    internal companion object {
        /**
         * Numeric status code, 200: OK
         */
        const val HTTP_OK = 200

        /**
         * Numeric status code, 201: Created
         */
        const val HTTP_CREATED = 201

        /**
         * Numeric status code, 202: Accepted
         */
        const val HTTP_ACCEPTED = 202

        /**
         * Numeric status code, 203: Not authoritative
         */
        const val HTTP_NOT_AUTHORITATIVE = 203

        /**
         * Numeric status code, 204: No content
         */
        const val HTTP_NO_CONTENT = 204

        /**
         * Numeric status code, 205: Reset
         */
        const val HTTP_RESET = 205

        /**
         * Numeric status code, 206: Partial
         */
        const val HTTP_PARTIAL = 206

        /**
         * Numeric status code, 300: Multiple choices
         */
        const val HTTP_MULT_CHOICE = 300

        /**
         * Numeric status code, 301 Moved permanently
         */
        const val HTTP_MOVED_PERM = 301

        /**
         * Numeric status code, 302: Moved temporarily
         */
        const val HTTP_MOVED_TEMP = 302

        /**
         * Numeric status code, 303: See other
         */
        const val HTTP_SEE_OTHER = 303

        /**
         * Numeric status code, 304: Not modified
         */
        const val HTTP_NOT_MODIFIED = 304

        /**
         * Numeric status code, 305: Use proxy.
         *
         * Like Firefox and Chrome, this class doesn't honor this response code.
         * Other implementations respond to this status code by retrying the request
         * using the HTTP proxy named by the response's Location header field.
         */
        const val HTTP_USE_PROXY = 305

        /**
         * Numeric status code, 400: Bad Request
         */
        const val HTTP_BAD_REQUEST = 400

        /**
         * Numeric status code, 401: Unauthorized
         */
        const val HTTP_UNAUTHORIZED = 401

        /**
         * Numeric status code, 402: Payment required
         */
        const val HTTP_PAYMENT_REQUIRED = 402

        /**
         * Numeric status code, 403: Forbidden
         */
        const val HTTP_FORBIDDEN = 403

        /**
         * Numeric status code, 404: Not found
         */
        const val HTTP_NOT_FOUND = 404

        /**
         * Numeric status code, 405: Bad Method
         */
        const val HTTP_BAD_METHOD = 405

        /**
         * Numeric status code, 406: Not acceptable
         */
        const val HTTP_NOT_ACCEPTABLE = 406

        /**
         * Numeric status code, 407: Proxy authentication required
         */
        const val HTTP_PROXY_AUTH = 407

        /**
         * Numeric status code, 408: Client Timeout
         */
        const val HTTP_CLIENT_TIMEOUT = 408

        /**
         * Numeric status code, 409: Conflict
         */
        const val HTTP_CONFLICT = 409

        /**
         * Numeric status code, 410: Gone
         */
        const val HTTP_GONE = 410

        /**
         * Numeric status code, 411: Length required
         */
        const val HTTP_LENGTH_REQUIRED = 411

        /**
         * Numeric status code, 412: Precondition failed
         */
        const val HTTP_PRECON_FAILED = 412

        /**
         * Numeric status code, 413: Entity too large
         */
        const val HTTP_ENTITY_TOO_LARGE = 413

        /**
         * Numeric status code, 414: Request too long
         */
        const val HTTP_REQ_TOO_LONG = 414

        /**
         * Numeric status code, 415: Unsupported type
         */
        const val HTTP_UNSUPPORTED_TYPE = 415

        /**
         * Numeric status code, 500: Internal error
         */
        const val HTTP_INTERNAL_ERROR = 500

        /**
         * Numeric status code, 501: Not implemented
         */
        const val HTTP_NOT_IMPLEMENTED = 501

        /**
         * Numeric status code, 502: Bad Gateway
         */
        const val HTTP_BAD_GATEWAY = 502

        /**
         * Numeric status code, 503: Unavailable
         */
        const val HTTP_UNAVAILABLE = 503

        /**
         * Numeric status code, 504: Gateway timeout
         */
        const val HTTP_GATEWAY_TIMEOUT = 504

        /**
         * Numeric status code, 505: Version not supported
         */
        const val HTTP_VERSION = 505

        /**
         * Numeric status code, 0: Server timeout
         */
        const val HTTP_SERVER_TIMEOUT = 1

        /**
         * Numeric status code, 1: e.g UnknownHostException: No address associated with hostname
         */
        const val HTTP_UNKNOWN_SERVER_ERROR = 2

        /**
         * Numeric status code, 2: No internet connection
         */
        const val HTTP_NO_NETWORK = 3

        /**
         * Numeric status code, 3: Connection refused by server
         */
        const val HTTP_CONNECTION_REFUSED = 4

        const val CONNECTION_ERROR = 5
    }
}