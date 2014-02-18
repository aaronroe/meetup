# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table invitation (
  id                        bigint not null,
  verification_code         varchar(255),
  inviter                   varchar(255),
  invited                   varchar(255),
  location                  varchar(255),
  topic                     varchar(255),
  responded                 boolean,
  constraint pk_invitation primary key (id))
;

create sequence invitation_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists invitation;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists invitation_seq;

