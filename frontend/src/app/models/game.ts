export interface Game {
  id: number;
  title: string;
  description: string;
  releaseDate: string;
  coverUrl: string;
  genre: string;
  platforms: Platform[];
}

export interface Platform {
  id: number;
  name: string;
}
