create table eventchange
(
    id   text      not null
        constraint eventchange_pk
            primary key,
    date timestamp not null,
    type smallint
);


create table guild
(
    idguild     varchar(255)         not null
        constraint guild_pk
            primary key,
    idchannel   varchar(255),
    urlplanning text,
    modifnotif  boolean default true not null,
    lessonnotif boolean default true not null
);

create table professor
(
    name varchar(255) not null,
    url  text,
    id   serial       not null
        constraint professor_pk
            primary key
);

create table lesson
(
    -- Only integer types can be auto increment
    id          varchar(255) not null
        constraint lesson_pk
            primary key,
    nom         text,
    datedebut   timestamp,
    datefin     timestamp,
    description text,
    classe      text,
    professor   integer
        constraint professor___fk
            references professor,
    id_unique   varchar(255)
);

create table movedlesson
(
    id             serial not null
        constraint movedlesson_pk
            primary key,
    previouslesson varchar(255)
        constraint movedlesson_lesson_id_fk
            references lesson,
    actuallesson   varchar(255)
        constraint movedlesson_lesson_id_fk_2
            references lesson
);

create table eventchange_lesson
(
    id_event        varchar(255)
        constraint eventchange_lesson_eventchange_id_fk
            references eventchange,
    id_moved_lesson integer
        constraint eventchange_lesson_movedlesson_id_fk
            references movedlesson
);
