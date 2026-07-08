
undefined8 main(void)

{
  int iVar1;
  int local_8c;
  char local_88 [32];
  char local_68 [32];
  undefined1 local_48 [32];
  undefined1 local_28 [32];
  
  local_8c = 0;
  puts("\n\n\n");
  puts("Welcome To ABC DEF Bank Managemet System!\n\n");
  printf("UserName : ");
  __isoc99_scanf(&DAT_001021f0,local_68);
  puts("\n");
  printf("Password : ");
  __isoc99_scanf(&DAT_001021f0,local_88);
  iVar1 = strcmp(local_68,"guest");
  if ((iVar1 == 0) && (iVar1 = strcmp(local_88,"guest"), iVar1 == 0)) {
    options();
    while (local_8c != 5) {
      printf("Your Choice : ");
      __isoc99_scanf(&DAT_00102216,&local_8c);
      if (local_8c == 1) {
        users();
      }
      else if (local_8c == 4) {
        printf("email : ");
        __isoc99_scanf(&DAT_001021f0,local_28);
        puts("\n");
        printf("Password : ");
        __isoc99_scanf(&DAT_001021f0,local_48);
        update(local_28,local_48);
      }
      else if ((local_8c == 3) || (local_8c == 2)) {
        puts("not available for guest account\n");
        system("clear");
        options();
      }
      else {
        puts("Wrong option\n");
        system("clear");
        options();
      }
    }
  }
  else {
    printf("Wrong username or password");
  }
  return 0;
}
