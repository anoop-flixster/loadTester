
package controllers;

import javax.annotation.Nullable;

import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.Response;
import play.libs.WS.WSRequestHolder;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {
    private final static String THIRD_PARTY_WS_HEROKU_URL = "http://flx-third-party-ws.herokuapp"
            + ".com";

    public static Promise<Result> index(@Nullable final Integer delay) {
        // Constructing the WebServices WS object
        final WSRequestHolder wsRequest = prepareWsRequestWithQueryParam(delay);

        final Promise<Response> responsePromise = wsRequest.get();

        // Non-blocking async IO
        final Promise<Result> result = responsePromise.map(toResult);
        return result;
    }

    public static Result blocking(@Nullable final Integer delay) {
        // Constructing the WebServices WS object
        final WSRequestHolder wsRequest = prepareWsRequestWithQueryParam(delay);

        final Promise<Response> responsePromise = wsRequest.get();

        /*
         * Blocking sync IO
         *
         * The 'third-party-ws' Heroku hosted app server will use a delay betwen [0, 10,000]
         * in case non is specified.
         *
         * Let's specify a delay of 30,000 ms or 30 seconds to accommodate the potentially slow
         * responses.
         */
        responsePromise.get(30000);

        return ok("Success ! You made a blocking sync IO call.");
    }

    private static WSRequestHolder prepareWsRequestWithQueryParam(@Nullable final Integer delay) {
        final WSRequestHolder wsRequest = WS.url(THIRD_PARTY_WS_HEROKU_URL);

        if (delay != null) {
            wsRequest.setQueryParameter("delay", delay.toString());
        }

        return wsRequest;
    }

    /**
     * Transforms a Response into a Result
     */
    private static Function<Response, Result> toResult =
            new Function<Response, Result>() {
                public Result apply(final Response response) {
                    return ok("Success ! You made a non-blocking async IO call.");
                }
            };
}
