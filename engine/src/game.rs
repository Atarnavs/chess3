mod piece;
use serde::Deserialize;
use std::collections::HashMap;

#[derive(Debug)]
#[derive(Deserialize)]
pub struct Square {
    x:u8,
    y:u8,
}
impl Square {
    pub fn print_square(&self) {
        println!("x is: {0}, y is {1}", self.x, self.y)
    }
}

#[derive(Deserialize)]
pub struct Move {
    starting_square:Square,
    ending_square:Square,
    piece_type:u8,
}
impl Move {
    pub fn print_move(&self) {
        print!("starting square is: ");
        self.starting_square.print_square();
        print!("ending square is: ");
        self.ending_square.print_square();
        println!("piece type is: {0}", self.piece_type);
    }
}
#[derive(Debug)]
pub struct Game {
    number_of_half_moves:u8,
    number_of_moves:u16,
    enpassant_square: Square,
    side_move: bool,
    castling_availability: [bool; 4],
    board: [[u8; 8]; 8],
    history:Vec<String>,
}

impl Game {
    fn fen_reader (&mut self, fen: String) {
        let mut space_counter:u8 = 0;
        let mut x:i8 = 0;
        let mut y:u8 = 7;
        let mut number_of_moves:String = String::new();
        let mut number_of_half_moves:String = String::new();

        for c in fen.chars() {
            //println!("Space counter equals: {space_counter}");
            println!("X is {}, and y is {}", x, y);
            if space_counter == 0 {
                match c {
                    'a'..='r' => self.set_piece(&c, &x, &y),
                    'A'..='R' => self.set_piece(&c, &x, &y),
                    '/' => {y -= 1; x -= 1;},
                    '1'..='8' => {x += c as i8 - 49},
                    ' ' => {space_counter += 1; continue;},
                    _ => continue,
                }
            }

            x = (x + 1)%8;

            if space_counter == 1 {
                match c {
                    'w' => self.side_move = true,
                    'b' => self.side_move = false,
                    ' ' => {space_counter += 1; continue;},
                    _ => continue,
                }
            }

            if space_counter == 2 {
                match c {
                    'K' => self.castling_availability[0] = true,
                    'Q' => self.castling_availability[1] = true,
                    'k' => self.castling_availability[2] = true,
                    'q' => self.castling_availability[3] = true,
                    '-' => self.castling_availability.iter_mut().for_each(|x| *x = false),
                    ' ' => {space_counter += 1; continue;},
                    _ => continue,
                }
            }

            if space_counter == 3 {
                match c {
                    'a'..='h' => self.enpassant_square.x = c as u8 - 97,
                    '1'..='8' => self.enpassant_square.y = c as u8 - 47,
                    ' ' => {space_counter += 1; continue;},
                    _ => continue,
                }
            }

            if space_counter == 4{
                if c != ' ' {
                    number_of_half_moves.push(c);
                    //println!("{c}")
                }
                else {
                    space_counter += 1;
                    continue;
                }
            }

            if space_counter == 5 {
                number_of_moves.push(c);
            }


        }
        self.number_of_half_moves = number_of_half_moves.parse().expect("fen should only have valid integers here");
        self.number_of_moves = number_of_moves.parse().expect("fen should only have valid integers here");
    }

    fn set_piece(&mut self, piece: &char, x: &i8, y: &u8) {
        let x = *x as usize;
        let y = *y as usize;
        //println!("Piece type is: {}", piece);
        if *piece == 'p' {
            self.board[y][x] = piece::BLACK | piece::PAWN;
        }
        else if *piece == 'r' {
            self.board[y][x] = piece::BLACK | piece::ROOK;
        }
        else if *piece == 'n' {
            self.board[y][x] = piece::BLACK | piece::KNIGHT;
        }
        else if *piece == 'b' {
            self.board[y][x] = piece::BLACK | piece::BISHOP;
        }
        else if *piece == 'q' {
            self.board[y][x] = piece::BLACK | piece::QUEEN;
        }
        else if *piece == 'k' {
            self.board[y][x] = piece::BLACK | piece::KING;
        }
        else if *piece == 'P' {
            self.board[y][x] = piece::WHITE | piece::PAWN;
        }
        else if *piece == 'R' {
            self.board[y][x] = piece::WHITE | piece::ROOK;
        }
        else if *piece == 'N' {
            self.board[y][x] = piece::WHITE | piece::KNIGHT;
        }
        else if *piece == 'B' {
            self.board[y][x] = piece::WHITE | piece::BISHOP;
        }
        else if *piece == 'Q' {
            self.board[y][x] = piece::WHITE | piece::QUEEN;
        }
        else if *piece == 'K' {
            self.board[y][x] = piece::WHITE | piece::KING;
        }
    }

    pub fn rule_check(&self, _piece_move: &Move) -> bool{
        true
    }

    pub fn make_move(&mut self, piece_move: &Move) {
        piece_move.print_move();
        let x_start:usize = piece_move.starting_square.x as usize;
        let y_start:usize = piece_move.starting_square.y as usize;
        let x_end:usize = piece_move.ending_square.x as usize;
        let y_end:usize = piece_move.ending_square.y as usize;
        let piece = self.board[y_start][x_start];
        self.board[y_start][x_start] = piece::EMPTY;
        self.board[y_end][x_end] = piece;
        self.history.push(self.make_fen());
    }

    fn make_fen(& self) -> String{
        //self.print_board();
        let pieces_map = HashMap::from([(piece::KING, 'k'), (piece::QUEEN, 'q'), 
        (piece::ROOK, 'r'), (piece::BISHOP, 'b'), (piece::KNIGHT, 'n'), (piece::PAWN, 'p'), (piece::EMPTY, ' ')]);
        let mut char_board = [[' '; 8]; 8];
        let mut colour;
        let mut piece;
        let mut to_add;
        let mut gaps = [0; 40];
        let mut non_space_counter = 0;
        let mut counter: isize = 0;
        for i in 0..8 {
            for j in 0..8 {
                piece = self.board[7 - i][j];
                colour = piece & 24;
                to_add = *pieces_map.get(&(piece & 7)).expect("the hashmap has all the values");
                if colour == 16 {
                    to_add.make_ascii_uppercase();
                }
                char_board[i][j] = to_add;
            }
        }
        print_char_board(&char_board);
        let mut new_fen = String::new();
        let mut new_fen_copy;
        for i in char_board {
            for j in i {
                new_fen.push(j);
            }
            new_fen.push('/');
        }
        new_fen.pop();
        new_fen_copy = new_fen.clone();
        //println!("{new_fen}");
        for c in new_fen.chars() {
            //print!("{c}, ");
            if c == ' ' {
                gaps[non_space_counter] += 1;
                new_fen_copy.replace_range((counter as usize)..(counter as usize) + 1, gaps[non_space_counter].to_string().as_str());
                //println!("{new_fen_copy}");
            }
            else {
                non_space_counter += 1;
            }
            counter += 1;
        }
        //println!();
        new_fen = new_fen_copy.clone();
        counter = 0;
        non_space_counter = 0;
        println!("{new_fen}");
        for c in new_fen.chars() {
            if c.is_ascii_digit() {
                if (c as u8 - 48) < gaps[non_space_counter]  {
                    new_fen_copy.replace_range((counter as usize)..(counter as usize) + 1, "");
                    counter -= 1
                }
                //counter -= 1;
            }
            else if !c.is_ascii_digit() {
                non_space_counter += 1;
            }
            counter += 1;
        }
        new_fen = new_fen_copy.clone();
        new_fen
    }

    pub fn build(fen: String) -> Game{
        println!("start");
        let mut game = Game {
            number_of_half_moves: 0,
            number_of_moves: 0,
            enpassant_square: Square {
                x: 0,
                y: 0,
            },
            side_move: true,
            castling_availability: [false; 4],
            board: [[0; 8]; 8],
            history: vec![fen.clone()],
        };
        println!("start 2");

        game.fen_reader(fen);
        println!("start almsot end");

        game
    }

    pub fn print_board(&self) {
        for i in self.board.iter().rev() {
            for j in i {
                print!("{j}, ");
            }
            println!();
        }
    }

    pub fn get_board(&self) -> String {
        self.history.last().unwrap().to_string()
    }
    pub fn unmake_move(&mut self) {
        self.history.pop();
        self.fen_reader(self.history.last().unwrap().to_string());
    }
    pub fn check_move(&self, potential_move: &Move) -> String {
        if self.rule_check(potential_move) {
            "PASS".to_owned()
        }
        else {
            "FAIL".to_owned()
        }
    }

}
pub fn print_char_board(array:&[[char; 8]; 8]) {
    for i in array {
        for c in i {
            print!("{c}, ");
        }
        println!();
    }
}