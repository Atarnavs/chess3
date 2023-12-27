// use actix_web::{error, post, web, App, Error, HttpResponse};
// use futures::StreamExt;
use serde::{Deserialize, Serialize};



use actix_web::{
    http::ConnectionType, middleware::Logger, web, App, HttpRequest, HttpResponse, HttpServer,
};
use chess::{game::Game, Application};
use std::sync::{Arc, Mutex};



use std::error::Error;

pub async fn get_board(req: HttpRequest, data: web::Data<Mutex<Application>>) -> String {
    // "BAD Hello world board!".to_owned()
    data.lock().unwrap().get_board(req).await
}

#[derive(Deserialize)]
pub struct Info {
    username: String,
    datax: i32,
}



pub async fn make_move(body: web::Json<Info>, data: web::Data<Mutex<Application>>) -> String {
    //req.
    println!("make move username: {0} datax: {1}", body.username, body.datax);
    // ("ANS:" + body.username())
    "MOVE Hello world board!".to_owned()
    // data.lock().unwrap().get_board(req).await
}


#[actix_web::main]
async fn main() -> std::io::Result<()> {
    // let mut game = Game::build(String::from("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    // print("hwllo real\n");

    let gameApp = web::Data::new(Mutex::new(Application::new(String::from(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
    ))));

    // app.main().await
    HttpServer::new(move || {
        let app = App::new()
            .app_data(gameApp.clone())
            .route("/", web::get().to(HttpResponse::Ok))
            .route("/get_board", web::get().to(get_board))
            .route("/make_move", web::post().to(make_move))
            .wrap(Logger::default());
        app
    })
    .bind(("127.0.0.1", 8080))?
    .run()
    .await
}
