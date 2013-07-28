import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Anagram {
	static class Node{
		static Node root;
		Node[] map;
		boolean isEnd;
		Node(){
			map = new Node[26];
		}
		void add(String w, int index){
			if(index == w.length()){
				isEnd = true;
				return;
			}
			int i = w.charAt(index)-'a';
			if(map[i] == null)
				map[i] = new Node();
			map[i].add(w, index+1);
		}
		void search(String w, boolean[] used, int spaces, int compare, char[] build, int index, int length, int minLength, List<String> list){
			if(length >= minLength && spaces == 0){ //TODO should all spaces have to be used
				if(isEnd) list.add(new String(build));
				//if(error == 0) return;
				return;
			}
			if(isEnd && spaces > 0){
				build[index] = '_';
				int i = index;
				for(; i>0; i--)
					if(build[i-1] == '_') break;
				root.search(w, used, spaces-1, i, build, index+1, length, minLength, list);
				build[index] = ' ';
			}
			boolean[] seen = new boolean[26];
			for(int x = 0; x<w.length(); x++)
				if(!used[x] && !seen[w.charAt(x)-'a'] && map[w.charAt(x)-'a'] != null && (compare == -1 || w.charAt(x) >= build[compare])){
					used[x] = true;
					seen[w.charAt(x)-'a'] = true;
					build[index] = w.charAt(x);
					int c = compare == -1 ? -1 : (build[compare+1] == '_' ? -1 : (w.charAt(x) == build[compare] ? compare+1 : -1));
					//System.out.println(new String(build) + " " + c);
					map[w.charAt(x)-'a'].search(w, used, spaces, c, build, index+1, length+1, minLength, list);
					build[index] = ' ';
					used[x] = false;
				}
			/*if(error > 0)
				for(int x = 0; x<26; x++)
					if(!seen[x] && map[x] != null){
						build[index] = (char) (x+'a');
						map[x].search(w, used, error-1, spaces, build, index+1, length+1, minLength, list);
						build[index] = '\0';
					}*/
		}
	}
	
	static String wordWrap(String str, int max){
		StringBuilder ans = new StringBuilder("");
		int len = 0;
		Scanner in = new Scanner(str);
		while(in.hasNext()){
			String w = in.next();
			if(len+w.length() > max){
				len = 0;
				ans.append("\n");
			}
			ans.append(w);
			ans.append(" ");
			len += w.length();
		}
		in.close();
		return ans.toString();
	}
	
	public static void main(String[] args) throws Exception{
		File directory = new File(".");
		for(File f: directory.listFiles())
			if(f.isFile() && f.getName().endsWith(".txt"))
				System.out.println(f.getName() + " " + f.length());
		
		Scanner in = new Scanner(System.in);
		System.out.println("Which dictionary would you like to use?");
		String fileName = in.next();
		File dicFile = new File(fileName);
		while(!dicFile.isFile()){
			System.out.println(fileName + " does not exist in the current directory. Try again.");
			fileName = in.next();
			dicFile = new File(fileName);
		}
		
		Scanner file = new Scanner(dicFile);
		Node root = new Node();
		Node.root = root;
		while(file.hasNext()){
			String w = file.next().toLowerCase().replaceAll("[^a-z]", "");
			if(w.equals("")) continue;
			root.add(w, 0);
		}
		file.close();
		while(true){
			System.out.println("Enter in the format: 'anagram words' or 'stop' to finish.");
			String cmd = in.next();
			cmd = cmd.toLowerCase().replaceAll("[^a-z]", "");
			if(cmd.equals("stop"))
				break;
			if(cmd.equals(""))
				continue;
			boolean[] used = new boolean[cmd.length()];
			//int error = in.nextInt();
			int spaces = in.nextInt()-1;
			char[] build = new char[cmd.length()+spaces];
			Arrays.fill(build, ' ');
			List<String> list = new ArrayList<String>();
			root.search(cmd, used, spaces, -1, build, 0, 0, cmd.length(), list);
			Collections.sort(list);
			System.out.println(wordWrap(list.toString(), 100));
		}
		in.close();
	}
}
