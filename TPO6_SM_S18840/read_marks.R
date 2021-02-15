suppressMessages(require(tidyverse))
suppressMessages(require(xlsx))

group_names <- readLines("groups.txt")
groups <- mapply(read.xlsx, "oceny.xlsx", group_names, stringsAsFactors=FALSE, SIMPLIFY=FALSE)
groups <- lapply(groups, function(frame) frame %>% select(-NA.))

names(groups) <- group_names

is_name_unique <- function(group, col_name, name)
{
  group %>%
    filter(.[, col_name] == name) -> with_name
  
  if(nrow(with_name) > 1)
  {
    print(paste("Jest kilku studentow w tej grupie o takim imieniu", name))
    print(with_name %>% select(Imie, Nazwisko, Indeks))
    return (FALSE)
  }
  
  if(nrow(with_name) == 0)
  {
    print(paste("Student o podanym imieniu nie nalezy do tej grupy", name))
    return (FALSE)
  }
  
  TRUE
}

set_mark <- function(group, col_name, index=-1L, name="", surname="", mark=1L, add = FALSE)
{
  if(index != -1L) {
    condition <- group$Indeks == index
  } else	if(surname != "" && is_name_unique(group, "Nazwisko", surname)) {
    condition <- group$Nazwisko == surname
  } else if(name != "" && is_name_unique(group, "Imie", name)) {
    condition <- group$Imie == name
  } else {
    print("Nie ma studenta o takich argumentach")
    return (group)
  }
  
  if(add) {
    group[condition, col_name] <- group[condition, col_name] + mark 
  } else {
    group[condition, col_name] <- mark
  }
  
  group
}

save_groups <- function(groups, path="oceny.xlsx")
{
  mask <- vector(mode="logical", length=length(groups))
  mask[2:length(groups)] <- TRUE
  mapply(write.xlsx, groups, path, names(groups), append=mask)
  TRUE
}