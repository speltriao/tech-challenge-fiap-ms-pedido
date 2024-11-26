variable "db_master_username" {
  description = "The master username for the Aurora Serverless cluster."
  type        = string
}

variable "db_master_password" {
  description = "The master password for the Aurora Serverless cluster."
  type        = string
  sensitive   = true
}