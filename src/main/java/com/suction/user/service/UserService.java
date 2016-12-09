package com.suction.user.service;

import com.suction.user.model.User;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;

public class UserService {

	JDBCClient jdbc;

	public UserService(JDBCClient jdbc) {

		super();
		System.out.println(">>>>>>>>>> Creating user service");
		this.jdbc = jdbc;
	}

	public void getUser(String userName, Handler<AsyncResult> handler) {
		jdbc.getConnection(asyncResult -> {
			if (asyncResult.failed()) {
				asyncResult.cause().printStackTrace();
			} else {
				SQLConnection connection = asyncResult.result();
				connection.queryWithParams("select * from user where username = ?", new JsonArray().add(userName),
						ar -> {

							if (ar.failed()) {
								ar.cause().printStackTrace();
								return;
							} else {
								handler.handle(Future.succeededFuture(ar.result()));
							}

						});
			}
		});
	}

}
