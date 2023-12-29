//use std::error::Error;
use actix_web::{http, middleware::Logger, web, App, HttpRequest, HttpResponse, HttpServer};
use game::Game;
use game::Move;
// use crate::{HttpCode, HttpVersion};

#[derive(Debug)]
pub struct Application {
    game: Game,
    count1: i32,
}

impl Application {
    pub fn new(fen: String) -> Self {
        Application { 
            game: Game::build(fen),
            count1: 0,
        }
    }

    pub fn get_board(&mut self, _req: HttpRequest) -> String {
        //self.count1 += 1;
        //format!("Hello world board {}!", self.count1).to_owned()
        self.game.get_board().to_owned()
    }

    pub fn check_move(&mut self, potential_move:&Move) -> &'static str {
        let check = self.game.rule_check(potential_move);
        if check {"PASS"}
        else {"NOT"}
    }

    pub fn make_move(&mut self, move_to_make:&Move) {
        self.game.make_move(move_to_make)
    }


    // async fn check_move(&self, _req: HttpRequest) -> HttpResponse {
    //     let mut resp = HttpResponse::Ok()
    //         .force_close() // <- Close connection on HttpResponseBuilder
    //         .finish();

    //     // Alternatively close connection on the HttpResponse struct
    //     resp.head_mut()
    //         .set_connection_type(http::ConnectionType::Close);

    //     resp
    // }

    pub fn print_board(&self) {
        self.game.print_board();
    }

    pub fn put_on_server(fen: String) -> &'static str {
        "GOOO"
    }
}

pub mod game;
pub mod info;
