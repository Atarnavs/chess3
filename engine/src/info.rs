use serde::{Deserialize, Serialize};
use crate::game;

#[derive(Deserialize)]
pub struct Info {
    pub username: String,
    pub datax: game::Move,
}
#[derive(Serialize)]
pub enum Responce {
    LegalMoveNoPromotion,
    LegalMovePromotion,
    IllegalMove,
}