variable "vpc_id" {}
variable "subnet_id_a" {}
variable "subnet_id_b" {}

provider "aws" {
  region = "us-east-1"
}

data "aws_vpc" "existing_vpc" {
  id = var.vpc_id
}

data "aws_subnet" "private_subnet_a" {
  id = var.subnet_id_a
}

data "aws_subnet" "private_subnet_b" {
  id = var.subnet_id_b
}

resource "aws_security_group" "sg_for_aurora" {
  vpc_id = data.aws_vpc.existing_vpc.id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "aurora-security-group"
  }
}

resource "aws_db_subnet_group" "db_subnet_group" {
  name       = "aurora-subnet-group"
  subnet_ids = [
    data.aws_subnet.private_subnet_a.id,
    data.aws_subnet.private_subnet_b.id
  ]

  tags = {
    Name = "aurora-subnet-group"
  }
}

resource "aws_rds_cluster" "serverless_aurora_pg" {
  engine             = "aurora-postgresql"
  engine_version     = "13.12"
  cluster_identifier = "serverless-aurora-pg-cluster"
  master_username    = var.db_master_username
  master_password    = var.db_master_password
  skip_final_snapshot = true
  db_subnet_group_name = aws_db_subnet_group.db_subnet_group.name
  vpc_security_group_ids = [aws_security_group.sg_for_aurora.id]
  database_name      = "galega"
  engine_mode        = "serverless"

  scaling_configuration {
    min_capacity = 2
    max_capacity = 4
    auto_pause   = false
  }

  tags = {
    Name = "serverless_aurora_pg"
  }
}

output "endpoint" {
  value = aws_rds_cluster.serverless_aurora_pg.endpoint
}