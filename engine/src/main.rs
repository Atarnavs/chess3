// use actix_web::{error, post, web, App, Error, HttpResponse};
// use futures::StreamExt;
use serde::{Deserialize, Serialize};
//use chess::game;

use actix_web::{
    http::ConnectionType, middleware::Logger, web, App, HttpRequest, HttpResponse, HttpServer,
};
use chess::{game, game::Game, Application};
use std::sync::{Arc, Mutex};
use chess::info::{Responce, Info};



use std::error::Error;

pub async fn get_board(req: HttpRequest, data: web::Data<Mutex<Application>>) -> String {
    // "BAD Hello world board!".to_owned()
    data.lock().unwrap().get_board(req)
}


pub async fn check_move(body: web::Json<Info>, data: web::Data<Mutex<Application>>) -> String {
    //req.
    println!("make move username: {0}", body.username);
    // ("ANS:" + body.username())
    let potential_move = &body.datax;
    data.lock().unwrap().check_move(potential_move).to_owned()
    //"MOVE Hello world board!".to_owned()
    // data.lock().unwrap().get_board(req).await
}
pub async fn make_move(body: web::Json<Info>, data: web::Data<Mutex<Application>>) -> String {
    data.lock().unwrap().make_move(&body.datax);
    "".to_owned()
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
            .route("/check_move", web::post().to(check_move))
            .wrap(Logger::default());
        app
    })
    .bind(("127.0.0.1", 8080))?
    .run()
    .await
}
