variable "region" {
  description = "region"
  default     = "ap-northeast-2"
}

variable "prefix" {
  description = "Prefix for all resources"
  default     = "Team02" // Team01 or Team02
}

variable "team_tag" {
  description = "team tag"
  default     = "devcos4-team02"
}

variable "app_1_domain" {
  description = "app_1 domain"
  default     = "ddobang.site" // API 서버 도메인
}

variable "app_1_db_name" {
  description = "app_1 db_name"
  default     = "team02_prod" // mysql 데이터베이스 이름
}

