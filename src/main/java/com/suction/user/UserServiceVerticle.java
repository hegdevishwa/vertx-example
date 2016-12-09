package com.suction.user;

import com.suction.user.service.UserService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class UserServiceVerticle extends AbstractVerticle {

	UserService userService;

	@Override
	public void start(Future<Void> future) {
		userService = new UserService(JDBCClient.createShared(vertx, config(), "auction"));

		startWeb(httpServer -> {
			if (httpServer.failed()) {
				httpServer.cause().printStackTrace();
				future.failed();
			} else {
				future.complete();
			}
		});

	}

	public void startWeb(Handler<AsyncResult<HttpServer>> nextHandler) {
		System.out.println(">>>>>>>>>>>>>>> startWeb");
		Router router = Router.router(vertx);
		router.route("/").handler(routingContext -> {
			routingContext.response().setStatusCode(200).setStatusMessage("OK").end("User service is running!!");
		});

		router.get("/api/users/:username").handler(this::getUserById);

		vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http-port", 1988),
				config().getString("host-name", "localhost"), nextHandler::handle);

	}

	private void getUserById(RoutingContext routingContext) {
		System.out.println(">>>>>>>>>>>>>>> getUserById");
		String userName = routingContext.pathParam("username");
		if (null == userName || userName.isEmpty()) {
			routingContext.response().setStatusCode(400).setStatusMessage("Invalid request. user name not avaialble")
					.end();
		} else {
			userService.getUser(userName, userResult -> {
				if (userResult.failed()) {
					routingContext.response().setStatusCode(500).setStatusMessage("Internal server error").end();
				} else {
					routingContext.response().setStatusCode(200)
							.putHeader("content-type", "application/json; charset=utf-8")
							.end(Json.encodePrettily(userResult.result()));
				}

			});
		}

	}
}
